package com.ljt.study.rabbitmq.spring;

import com.ljt.study.YamlPropertySourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author LiJingTang
 * @date 2021-05-13 19:06
 */
@Slf4j
@Configuration
@PropertySource(value = "classpath:rabbitmq/application.yml", factory = YamlPropertySourceFactory.class)
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
     */
    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addQueues(oo());
        return container;
    }

}
