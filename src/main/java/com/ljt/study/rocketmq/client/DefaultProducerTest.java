package com.ljt.study.rocketmq.client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ljt.study.rocketmq.client.RocketMQUtils.*;

/**
 * @author LiJingTang
 * @date 2021-06-19 09:50
 */
@Slf4j
class DefaultProducerTest {

    private static DefaultMQProducer producer;

    @BeforeAll
    @SneakyThrows
    static void beforeAll() {
        producer = new DefaultMQProducer(TEST_CLIENT_GROUP);
        producer.setNamesrvAddr(NAME_SERVER);
        // 先start 再send
        producer.start();
    }

    @AfterAll
    @SneakyThrows
    static void AfterAll() {
        log.info("开始消费消息");
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(TEST_CLIENT_GROUP);
        consumer.setNamesrvAddr(NAME_SERVER);
        consumer.subscribe(TEST_CLIENT_TOPIC, "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            log.info("consumeMessage ... ");
            msgs.forEach(msg -> log.info("收到消息：{}", new String(msg.getBody())));
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();

        TimeUnit.SECONDS.sleep(10);
    }


    @Test
    @SneakyThrows
    void send() {
        Message msg = new Message(TEST_CLIENT_TOPIC, "我是一条RocketMQ消息".getBytes(StandardCharsets.UTF_8));
        SendResult result = producer.send(msg);
        log.info("同步发送一条消息：{}", result);
    }

    @Test
    @SneakyThrows
    void sendMulti() {
        List<Message> msgs = IntStream.rangeClosed(1, 3).mapToObj(i -> new Message(TEST_CLIENT_TOPIC,
                String.format("我是第%s条RocketMQ消息", i).getBytes(StandardCharsets.UTF_8))).collect(Collectors.toList());
        SendResult result = producer.send(msgs);
        log.info("同步发送{}条消息：{}", msgs.size(), result);
    }

    @Test
    @SneakyThrows
    void sendAsync() {
        Message msg = new Message(TEST_CLIENT_TOPIC, "我是一条异步RocketMQ消息".getBytes(StandardCharsets.UTF_8));
        producer.send(msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult result) {
                log.info("异步发送一条消息：{}", result);
            }

            @Override
            public void onException(Throwable e) {
                log.info("异步发送一条消息异常", e);
            }
        });

        log.info("异步发送消息");
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    @SneakyThrows
    void sendOneWay() {
        Message msg = new Message(TEST_CLIENT_TOPIC, "我是一条RocketMQ:Oneway消息".getBytes(StandardCharsets.UTF_8));
        producer.sendOneway(msg);
    }

}
