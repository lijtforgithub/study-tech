package com.ljt.study.rocketmq.client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;
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
        producer = new DefaultMQProducer(CLIENT_GROUP);
        producer.setNamesrvAddr(NAME_SERVER);
        producer.setSendMsgTimeout((int) TimeUnit.MINUTES.toMillis(1));
        // 先start 再send
        producer.start();
    }


    @Test
    @SneakyThrows
    void send() {
        Message msg = new Message(CLIENT_TOPIC, "同步发送消息".getBytes(StandardCharsets.UTF_8));
        SendResult result = producer.send(msg);
        log.info("同步发送一条消息：{}", result);
    }

    @Test
    @SneakyThrows
    void sendMulti() {
        List<Message> msgList = IntStream.rangeClosed(1, 3).mapToObj(i -> new Message(CLIENT_TOPIC,
                String.format("第%s条RocketMQ消息", i).getBytes(StandardCharsets.UTF_8))).collect(Collectors.toList());
        SendResult result = producer.send(msgList);
        log.info("同步发送{}条消息：{}", msgList.size(), result);
    }

    @Test
    @SneakyThrows
    void sendAsync() {
        CountDownLatch latch = new CountDownLatch(1);
        Message msg = new Message(CLIENT_TOPIC, "异步发送消息".getBytes(StandardCharsets.UTF_8));
        producer.send(msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult result) {
                log.info("异步发送消息：{}", result);
                latch.countDown();
            }

            @Override
            public void onException(Throwable e) {
                log.info("异步发送消息异常", e);
            }
        });

        log.info("异步发送消息");
        latch.await();
    }

    @Test
    @SneakyThrows
    void sendOneWay() {
        Message msg = new Message(CLIENT_TOPIC, "Oneway发送消息".getBytes(StandardCharsets.UTF_8));
        producer.sendOneway(msg);
    }

    @Test
    @SneakyThrows
    void sendWithTag() {
        final String tag = "Tag-A";
        Message msg = new Message(CLIENT_TOPIC, tag, "bizId", "Tag消息".getBytes(StandardCharsets.UTF_8));
        producer.send(msg);

        // 同一个组不能启动多个消费者 The consumer group[test_client_group] has been created before, specify another name please
        DefaultPushConsumerTest.consumeMessage(MessageSelector.byTag(tag));
    }

    @Test
    @SneakyThrows
    void sendUserProp() {
        final String age = "age";
        List<Message> msgList = IntStream.rangeClosed(1, 10).mapToObj(i -> {
            Message msg = new Message(CLIENT_TOPIC, ("消息-" + i).getBytes(StandardCharsets.UTF_8));
            msg.putUserProperty(age, String.valueOf(i));
            return msg;
        }).collect(Collectors.toList());
        producer.send(msgList);

        // 同一个topic 其他的group都会消费一次 消费者要在发送之前启动
        DefaultPushConsumerTest.consumeMessage(MessageSelector.bySql(String.format("%s >= %s and %s <= %s", age, 6, age, 8)));
    }

    @Test
    void setRetry() {
        // 默认2次
        producer.setRetryTimesWhenSendFailed(1);
        producer.setRetryTimesWhenSendAsyncFailed(1);
        // 默认false
        producer.setRetryAnotherBrokerWhenNotStoreOK(true);
    }

    @Test
    @SneakyThrows
    void sendWithQueue() {
//        producer.setDefaultTopicQueueNums();
        // 一个topic(逻辑单位)默认4个queue(物理单位保证消息FIFO)
        producer.send(new Message(CLIENT_TOPIC, "指定队列消息".getBytes(StandardCharsets.UTF_8)), (mqs, msg, arg) -> {
            mqs.forEach(q -> log.info(q.toString()));
            return mqs.get(Integer.parseInt(String.valueOf(arg)));
        }, "0");
    }

    @Test
    @SneakyThrows
    void sendDelay() {
        // messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        Message message = new Message(CLIENT_TOPIC, "延迟消费消息".getBytes(StandardCharsets.UTF_8));
        message.setDelayTimeLevel(5);
        producer.send(message);
        producer.send(new Message(CLIENT_TOPIC, "正常消费消息".getBytes(StandardCharsets.UTF_8)));
    }

}
