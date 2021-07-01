package com.ljt.study.rocketmq.client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;

import static com.ljt.study.rocketmq.client.RocketMQUtils.*;

/**
 * @author LiJingTang
 * @date 2021-06-29 11:38
 */
@Slf4j
public class DefaultConsumerTest {

    public static void main(String[] args) {
        pushConsumer(true);
    }

    @SneakyThrows
    private static void pushConsumer(boolean isOrder) {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(DEF_GROUP);
        consumer.setNamesrvAddr(NAME_SERVER);
        consumer.subscribe(DEF_TOPIC, "*");

        if (isOrder) {
            consumer.registerMessageListener(orderMessageListener);
        } else {
            consumer.registerMessageListener(concurrentMessageListener);
        }

        consumer.start();
        log.info("开始消费消息");
    }

    static MessageListenerConcurrently concurrentMessageListener = (msgList, context) -> {
        log.info("concurrent consumeMessage ... ");
        msgList.forEach(msg -> log.info("{} 收到消息：{}", Thread.currentThread().getName(), new String(msg.getBody())));
        log.info(context.getMessageQueue().toString());
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    };

    static MessageListenerOrderly orderMessageListener = (msgList, context) -> {
        log.info("order consumeMessage ... ");
        msgList.forEach(msg -> log.info("{} 收到消息：{}", Thread.currentThread().getName(), new String(msg.getBody())));
        log.info(context.getMessageQueue().toString());
        return ConsumeOrderlyStatus.SUCCESS;
    };

}
