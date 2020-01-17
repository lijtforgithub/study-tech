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
@PropertySource(value = "classpath:rabbitmq/mq.yml", factory = YamlPropertySourceFactory.class)
public class DelayConfig {

    public static void main(String[] args) {
        SpringApplication.run(DelayConfig.class);
    }

    private static final String KEY_DLRK = "x-dead-letter-routing-key";
    private static final String KEY_DLX = "x-dead-letter-exchange";
    private static final String KEY_TTL = "x-message-ttl";

    /**
     * TTL配置在消息上的缓冲队列
     */
    static final String QUEUE_TTL_MSG = "test.delay.ttl.message";
    /**
     * TTL配置在队列上的缓冲队列
     */
    static final String QUEUE_TTL = "test.delay.ttl.queue";
    /**
     * 队列TTL时间 单位秒
     */
    static final int TTL_SED = 1 * 60;
    /**
     * DLX exchange
     */
    private static final String EXCHANGE_DLX = "test.delay.exchange.ddl";
    /**
     * 实际消费队列 DLX转发队列
     */
    private static final String QUEUE_DELAY = "test.delay.queue.consumer";

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
        args.put(KEY_DLX, EXCHANGE_DLX);
        args.put(KEY_DLRK, QUEUE_DELAY);
        Queue queue = new Queue(QUEUE_TTL_MSG, true, false, false, args);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    /**
     * TTL配置在队列上的缓冲队列
     */
    @Bean
    public Queue ttlQueue() {
        return QueueBuilder.durable(QUEUE_TTL)
                .withArgument(KEY_DLX, EXCHANGE_DLX) // DLX，dead letter发送到的exchange
                .withArgument(KEY_DLRK, QUEUE_DELAY) // dead letter携带的routing key
                .withArgument(KEY_TTL, TTL_SED * 1000L) // 设置队列的过期时间
                .build();
    }

    /**
     * 实际消费队列 DLX转发队列
     */
    @Bean
    public Queue consumerQueue(RabbitAdmin rabbitAdmin) {
        return new Queue(QUEUE_DELAY);
    }

    /**
     * DLX
     */
    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(EXCHANGE_DLX);
    }

    /**
     * 绑定DLX和实际消费队列
     */
    @Bean
    public Binding dlxBinding(Queue consumerQueue, DirectExchange delayExchange) {
        return BindingBuilder.bind(consumerQueue)
                .to(delayExchange)
                .with(QUEUE_DELAY);
    }

    @Bean
    public SimpleMessageListenerContainer buildResultRecvContainer(Queue consumerQueue, ConsumerListener consumerListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(consumerQueue);
        container.setMessageListener(consumerListener);
        return container;
    }

}
