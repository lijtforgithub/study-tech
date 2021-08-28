package com.ljt.study.rabbitmq.spring.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2021-08-27 14:43
 */
@Slf4j
@SpringBootTest
class RabbitTemplateTest {

    private static final String QUEUE = "oo";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void send() {
        for (int i = 1; i < 11; i++) {
            rabbitTemplate.convertAndSend(QUEUE, String.valueOf(i));
        }
    }

    @Test
    void receive() {
        Message msg = rabbitTemplate.receive(QUEUE);
        log.info("接收消息：{}", Objects.nonNull(msg) ? new String(msg.getBody()) : null);
    }

}
