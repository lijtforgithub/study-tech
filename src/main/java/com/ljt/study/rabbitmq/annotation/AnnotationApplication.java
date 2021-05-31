package com.ljt.study.rabbitmq.annotation;

import com.ljt.study.YamlPropertySourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * @author LiJingTang
 * @date 2021-05-14 09:46
 */
@Slf4j
@SpringBootApplication
@PropertySource(value = "classpath:rabbitmq/mq.yml", factory = YamlPropertySourceFactory.class)
public class AnnotationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnnotationApplication.class);
    }

    @RabbitListener(queuesToDeclare = @Queue("test_queue"))
    public void onMessage(Message message) {
        log.info(new String(message.getBody()));
    }

}
