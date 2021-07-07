package com.ljt.study.rabbitmq.delay;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.MessageFormat;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author LiJingTang
 * @date 2020-01-04 14:14
 */
@Slf4j
@SpringBootTest
class DelayTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ConsumerListener consumerListener;

    /**
     * 消息上设置ttl
     */
    @Test
    void ttlMsg() {
        consumerListener.setLatch(new CountDownLatch(1));

        int t = 30;
        String message = MessageFormat.format("我是一个延迟【{0, number,#}】秒消费的消息", t);
        rabbitTemplate.convertAndSend(DelayConfig.QUEUE_TTL_MSG, message, msg -> {
            msg.getMessageProperties().setExpiration(String.valueOf(SECONDS.toMillis(t)));
            return msg;
        });
        printMsg(message);
    }

    /**
     * 多个消息 在消息上设置ttl
     * 如果第一个消息时间长后面时间短  也要等第一个消息被转发 后面的消息才能转发 因为在消息上的TTL是在消息被消费时判断的
     * 所有要同一延迟时间用一个队列
     */
    @Test
    void ttlMultiMsg() {
        final int count = 3;
        consumerListener.setLatch(new CountDownLatch(count));

        for (int i = count; i > 0; i--) {
            int t = i * 10;
            String message = MessageFormat.format("我是一个设置延迟【{0, number,#}】秒消费的消息", t);
            rabbitTemplate.convertAndSend(DelayConfig.QUEUE_TTL_MSG, message, msg -> {
                msg.getMessageProperties().setExpiration(String.valueOf(SECONDS.toMillis(t)));
                return msg;
            });
            printMsg(message);
        }
    }

    /**
     * 队列上设置ttl
     */
    @Test
    void ttlQueue() {
        consumerListener.setLatch(new CountDownLatch(1));

        String message = MessageFormat.format("我是一个延迟【{0, number,#}】秒队列上的消息", DelayConfig.TTL_SED);
        rabbitTemplate.convertAndSend(DelayConfig.QUEUE_TTL, message);
        printMsg(message);
    }

    /**
     * 消息和队列同时设置ttl
     * 以时间小的为准
     */
    @Test
    void ttlQueueMsg() {
        consumerListener.setLatch(new CountDownLatch(2));

        String content = "我是一个延迟【{0, number,#}】秒队列上设置了延迟【{1, number,#}】秒消费的消息";
        int t1 = (int) (DelayConfig.TTL_SED * 0.5);
        String message = MessageFormat.format(content, DelayConfig.TTL_SED, t1);
        rabbitTemplate.convertAndSend(DelayConfig.QUEUE_TTL, message, msg -> {
            msg.getMessageProperties().setExpiration(String.valueOf(SECONDS.toMillis(t1)));
            return msg;
        });
        printMsg(message);


        int t2 = (int) (DelayConfig.TTL_SED * 1.5);
        message = MessageFormat.format(content, DelayConfig.TTL_SED, t2);
        rabbitTemplate.convertAndSend(DelayConfig.QUEUE_TTL, message, msg -> {
            msg.getMessageProperties().setExpiration(String.valueOf(SECONDS.toMillis(t2)));
            return msg;
        });
        printMsg(message);
    }

    @AfterEach
    @SneakyThrows
    private void afterEach() {
        consumerListener.getLatch().await();
    }

    private void printMsg(String message) {
        log.info("发送消息：{}", message);
    }

}
