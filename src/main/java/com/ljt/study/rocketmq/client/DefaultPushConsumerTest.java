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
        consumer.subscribe(CLIENT_TOPIC, ObjectUtils.defaultIfNull(selector, MessageSelector.byTag("*")));
        if (Objects.nonNull(model)) {
            // 默认 MessageModel.CLUSTERING
            consumer.setMessageModel(model);
        }

        consumer.registerMessageListener(DefaultConsumerTest.concurrentMessageListener);

        consumer.setConsumeThreadMax(2);
        consumer.setConsumeThreadMin(1);

        // 默认15分钟
        consumer.setConsumeTimeout(5);

        consumer.setMaxReconsumeTimes(5);
        consumer.setConsumeMessageBatchMaxSize(3);
        consumer.setSuspendCurrentQueueTimeMillis(TimeUnit.SECONDS.toMillis(5));
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        consumer.start();
        log.info("开始消费消息");

        TimeUnit.SECONDS.sleep(30);
    }

}
