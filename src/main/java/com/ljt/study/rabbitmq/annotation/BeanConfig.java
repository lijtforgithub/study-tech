package com.ljt.study.rabbitmq.annotation;

import com.ljt.study.YamlPropertySourceFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author LiJingTang
 * @date 2021-05-13 19:06
 */
@Configuration
@PropertySource(value = "classpath:rabbitmq/mq.yml", factory = YamlPropertySourceFactory.class)
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
