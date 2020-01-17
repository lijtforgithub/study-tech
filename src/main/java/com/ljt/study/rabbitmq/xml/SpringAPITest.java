package com.ljt.study.rabbitmq.xml;

import com.ljt.study.Constant;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2020-01-17 10:39
 */
public class SpringAPITest {

    private static final String EXCHANGE_NAME = "test.spring.topic.exchange";
    private static final String QUEUE_NAME = "test.spring.api";
    private static final String ROUTING_KEY = "key.spring.*";

    public static void main(String[] args) throws InterruptedException {
        ConnectionFactory connectionFactory = new CachingConnectionFactory(Constant.LOCAL_HOST);
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);

        Queue queue = new Queue(QUEUE_NAME, false, true, false);
        admin.declareQueue(queue);
        TopicExchange exchange = new TopicExchange(EXCHANGE_NAME);
        admin.declareExchange(exchange);
        admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY));

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        Object listener = new Object() {
            public void handleMessage(String message) {
                System.out.println("接收消息：" + message);
            }
        };

        MessageListenerAdapter adapter = new MessageListenerAdapter(listener);
        container.setMessageListener(adapter);
        container.setQueueNames(QUEUE_NAME);
        container.start();

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "key.spring.api", "Hello, world!发送消息必须是topic路由，仅接收队列绑定不行");

        TimeUnit.SECONDS.sleep(10);

        container.stop();
        System.exit(0);
    }

}
