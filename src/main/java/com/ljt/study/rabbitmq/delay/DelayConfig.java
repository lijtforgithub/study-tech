package com.ljt.study.rabbitmq.delay;

import com.google.common.collect.Maps;
import com.ljt.study.YamlPropertySourceFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author LiJingTang
 * @date 2020-01-04 12:54
 */
@Configuration
@PropertySource(value = "classpath:rabbitmq/application.yml", factory = YamlPropertySourceFactory.class)
public class DelayConfig {

    public static final String KEY_DLK = "x-dead-letter-routing-key";
    public static final String KEY_DLX = "x-dead-letter-exchange";
    public static final String KEY_TTL = "x-message-ttl";

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
    static final int TTL_SED = 60;
    /**
     * DLX exchange
     */
    private static final String EXCHANGE_DLX = "test.delay.exchange.ddl";
    /**
     * 实际消费队列 DLX转发队列
     */
    private static final String QUEUE_DELAY = "test.delay.queue.consumer";


    /**
     * TTL配置在消息上的缓冲队列
     */
    @Bean
    public Queue ttlMessageQueue(AmqpAdmin amqpAdmin) {
        Map<String, Object> args = Maps.newHashMapWithExpectedSize(2);
        args.put(KEY_DLX, EXCHANGE_DLX);
        args.put(KEY_DLK, QUEUE_DELAY);
        Queue queue = new Queue(QUEUE_TTL_MSG, true, false, false, args);
        amqpAdmin.declareQueue(queue);
        return queue;
    }

    /**
     * TTL配置在队列上的缓冲队列
     */
    @Bean
    public Queue ttlQueue() {
        return QueueBuilder.durable(QUEUE_TTL)
                // DLX，dead letter发送到的exchange
                .withArgument(KEY_DLX, EXCHANGE_DLX)
                // dead letter携带的routing key
                .withArgument(KEY_DLK, QUEUE_DELAY)
                // 设置队列的过期时间
                .withArgument(KEY_TTL, SECONDS.toMillis(TTL_SED))
                .build();
    }

    /**
     * 实际消费队列 DLX转发队列
     */
    @Bean
    public Queue consumerQueue() {
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
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, Queue consumerQueue,
                                                    ConsumerListener consumerListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(consumerQueue);
        container.setMessageListener(consumerListener);
        return container;
    }

}
