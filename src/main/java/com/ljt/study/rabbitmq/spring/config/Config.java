package com.ljt.study.rabbitmq.spring.config;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareBatchMessageListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2021-05-13 19:06
 */
@Slf4j
@Configuration
class Config {

    @Bean
    public FanoutExchange xx() {
        return new FanoutExchange("xx");
    }

    @Bean
    public Queue oo() {
        return new Queue("oo");
    }

    @Bean
    public Binding xxoo() {
        return BindingBuilder.bind(oo()).to(xx());
    }

    /**
     * Container实现了SmartLifecycle
     * DirectMessageListenerContainer
     * 配置了Container才会触发RabbitAdmin的initialize方法
     *
     * spring cloud 配置中心的container不在IOC容器中
     * org.springframework.cloud.stream.binder.rabbit.RabbitMessageChannelBinder#createConsumerEndpoint
     *
     * this.channel.basicQos(this.prefetchCount);
     * org.springframework.amqp.rabbit.listener.BlockingQueueConsumer#setQosAndCreateConsumers()
     */
    @Bean
    public SimpleMessageListenerContainer simpleContainer(ConnectionFactory connectionFactory, MessageRecv messageRecv) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addQueues(oo());
        container.setMessageListener(messageRecv);
        container.setConcurrency("1-3");
        container.setPrefetchCount(3);
        container.setBatchSize(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        container.setErrorHandler(t -> log.info("自定义异常处理：{}", t.getMessage()));
        container.setAfterReceivePostProcessors(message -> {
            log.info("MessagePostProcessor: {}", new String(message.getBody()));
            return message;
        });

        return container;
    }

    @Bean
    MessageRecv messageRecv() {
        return new MessageRecv();
    }

}

/**
 * @see ChannelAwareBatchMessageListener
 */
@Slf4j
class MessageRecv implements ChannelAwareMessageListener {

    private AcknowledgeMode mode;

    @SneakyThrows
    @Override
    public void onMessage(Message message, Channel channel) {
        String msg = new String(message.getBody());

        log.info("开始：{}", msg);

        TimeUnit.SECONDS.sleep(4);

        log.info("结束：{}", msg);

        if (AcknowledgeMode.MANUAL == mode) {
            TimeUnit.SECONDS.sleep(1);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("ACK：{}", msg);
        }
    }

    @Override
    public void containerAckMode(AcknowledgeMode mode) {
        log.info("消息确认方式：{}", mode);
        this.mode = mode;
    }

}
