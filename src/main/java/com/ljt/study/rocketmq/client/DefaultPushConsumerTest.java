package com.ljt.study.rocketmq.client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.ljt.study.rocketmq.client.RocketMQUtils.*;

/**
 * @author LiJingTang
 * @date 2021-06-28 16:18
 */
@Slf4j
public class DefaultPushConsumerTest {

    @SneakyThrows
    public static void main(String[] args) {
        consumeMessage(MessageModel.CLUSTERING);
    }

    @SneakyThrows
    static void consumeMessage(MessageModel model) {
        consumeMessage(model, null, null);
    }

    @SneakyThrows
    static void consumeMessage(MessageSelector selector) {
        consumeMessage(null, selector, CLIENT_GROUP_X);
    }

    @SneakyThrows
    private static void consumeMessage(MessageModel model, MessageSelector selector, String groupName) {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(StringUtils.defaultIfBlank(groupName, CLIENT_GROUP));
        consumer.setNamesrvAddr(NAME_SERVER);
        /**
         * 可以订阅多个topic
         */
        consumer.subscribe(CLIENT_TOPIC, ObjectUtils.defaultIfNull(selector, MessageSelector.byTag("*")));
        if (Objects.nonNull(model)) {
            // 默认 MessageModel.CLUSTERING
            consumer.setMessageModel(model);
        }

        consumer.registerMessageListener(DefaultConsumerTest.concurrentMessageListener);

        consumer.setConsumeThreadMax(3);
        consumer.setConsumeThreadMin(3);

        // 默认15分钟
        consumer.setConsumeTimeout(5);
//        consumer.setMaxReconsumeTimes(5);
        consumer.setPullBatchSize(100);
        consumer.setConsumeMessageBatchMaxSize(5);
        consumer.setSuspendCurrentQueueTimeMillis(TimeUnit.SECONDS.toMillis(5));

        /**
         * CONSUME_FROM_LAST_OFFSET：第一次启动从队列最后位置消费，后续再启动接着上次消费的进度开始消费
         * CONSUME_FROM_FIRST_OFFSET：第一次启动从队列初始位置消费，后续再启动接着上次消费的进度开始消费
         * CONSUME_FROM_TIMESTAMP：第一次启动从指定时间点位置消费，后续再启动接着上次消费的进度开始消费
         * 第一次启动是指从来没有消费过的消费者，如果该消费者消费过，那么会在broker端记录该消费者的消费位置，如果该消费者挂了再启动，那么自动从上次消费的进度开始，RemoteBrokerOffsetStore
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

        consumer.start();
        log.info("开始消费消息");

        TimeUnit.SECONDS.sleep(30);
    }

}
