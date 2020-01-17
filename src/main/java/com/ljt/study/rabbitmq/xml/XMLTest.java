package com.ljt.study.rabbitmq.xml;

import com.ljt.study.AbstractTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2020-01-17 10:05
 */
public class XMLTest extends AbstractTest {

    @Test
    public void testBean() {
        String key = "key.spring.helloworld";
        System.setProperty("queueName", "test.spring.helloworld");
        System.setProperty("key", key);
        setApplicationContext("bean");
        RabbitTemplate rabbitTemplate = this.applicationContext.getBean(RabbitTemplate.class);

        rabbitTemplate.convertAndSend(key, "Hello World!");
    }

    @Test
    public void testNamespace() {
        String key = "key.spring.namespace";
        System.setProperty("queueName", "test.spring.namespace");
        System.setProperty("key", key);
        setApplicationContext("namespace");
        RabbitTemplate rabbitTemplate = this.applicationContext.getBean(RabbitTemplate.class);

        rabbitTemplate.convertAndSend(key, "Hello World!namespace");
    }

    @AfterAll
    public static void after() {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
