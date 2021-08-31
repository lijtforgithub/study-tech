package com.ljt.study.rabbitmq.spring.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.channels.Channel;

/**
 * @author LiJingTang
 * @date 2021-05-14 09:46
 */
@Slf4j
@SpringBootApplication
class BootApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class);
    }

//    @RabbitListener(queuesToDeclare = @Queue("test_queue"))
    public void onMessage(Message message, Channel channel) {
        log.info(new String(message.getBody()));
    }

    /*@EventListener(condition = "event.eventType == 'queue.created'")
    void listener(BrokerEvent event) {
        log.info(event.toString());
    }*/

}
