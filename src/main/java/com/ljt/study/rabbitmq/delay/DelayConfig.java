package com.ljt.study.rabbitmq.delay;

import com.google.common.collect.Maps;
import com.ljt.study.YamlPropertySourceFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

/**
 * @author LiJingTang
 * @date 2020-01-04 12:54
 */
@SpringBootApplication
@PropertySource(value = "classpath:rabbitmq.yml", factory = YamlPropertySourceFactory.class)
public class DelayConfig {

    public static void main(String[] args) {
        SpringApplication.run(DelayConfig.class);
    }

    private static final String KEY_DLRK = "x-dead-letter-routing-key";
    private static final String KEY_DLX = "x-dead-letter-exchange";

    /**
     * TTL配置在消息上的缓冲队列
     */
    static final String TTL_MSG_QUEUE = "test.delay.queue.message.ttl";
    /**
     * TTL配置在队列上的缓冲队列
     */
    static final String TTL_QUEUE = "test.delay.queue.ttl";
    /**
     * 队列TTL时间 单位秒
     */
    static final int TTL_QUEUE_SED = 1 * 60;
    /**
     * DLX exchange
     */
    private static final String DLX_EXCHANGE = "test.delay.exchange.ddl";
    /**
     * 实际消费队列 DLX转发队列
     */
    private static final String DELAY_QUEUE = "test.delay.queue.consumer";

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * TTL配置在消息上的缓冲队列
     */
    @Bean
    public Queue ttlMessageQueue(RabbitAdmin rabbitAdmin) {
        Map<String, Object> args = Maps.newHashMapWithExpectedSize(2);
        args.put(KEY_DLX, DLX_EXCHANGE);
        args.put(KEY_DLRK, DELAY_QUEUE);
        Queue queue = new Queue(TTL_MSG_QUEUE, true, false, false, args);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    /**
     * TTL配置在队列上的缓冲队列
     */
    @Bean
    public Queue ttlQueue() {
        return QueueBuilder.durable(TTL_QUEUE)
                .withArgument(KEY_DLX, DLX_EXCHANGE) // DLX，dead letter发送到的exchange
                .withArgument(KEY_DLRK, DELAY_QUEUE) // dead letter携带的routing key
                .withArgument("x-message-ttl", TTL_QUEUE_SED * 1000L) // 设置队列的过期时间
                .build();
    }

    /**
     * 实际消费队列 DLX转发队列
     */
    @Bean
    public Queue consumerQueue(RabbitAdmin rabbitAdmin) {
        return new Queue(DELAY_QUEUE);
    }

    /**
     * DLX
     */
    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    /**
     * 绑定DLX和实际消费队列
     */
    @Bean
    public Binding dlxBinding(Queue consumerQueue, DirectExchange delayExchange) {
        return BindingBuilder.bind(consumerQueue)
                .to(delayExchange)
                .with(DELAY_QUEUE);
    }

    @Bean
    public SimpleMessageListenerContainer buildResultRecvContainer(Queue consumerQueue, ConsumerListener consumerListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(consumerQueue);
        container.setMessageListener(consumerListener);
        return container;
    }

}
