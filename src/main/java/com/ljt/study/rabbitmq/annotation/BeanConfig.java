package com.ljt.study.rabbitmq.annotation;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LiJingTang
 * @date 2021-05-13 19:06
 */
@Configuration
public class BeanConfig {

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
     * DirectMessageListenerContainer
     *
     * 配置了Container才会触发RabbitAdmin的initialize方法
     */
    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addQueues(oo());
        return container;
    }

}
