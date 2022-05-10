package com.ljt.study.rocketmq.client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
        // 长轮询 从ProcessQueue获取
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(DEF_GROUP);
        consumer.setNamesrvAddr(NAME_SERVER);
        consumer.subscribe(DEF_TOPIC, "*");
        consumer.subscribe(CLIENT_TOPIC, "*");
        // 顺序消费重投间隔时间
        consumer.setSuspendCurrentQueueTimeMillis(TimeUnit.SECONDS.toMillis(5));
        // 顺序并行都有效 最大重投次数 不包含第一次接收
        consumer.setMaxReconsumeTimes(3);
        consumer.setConsumeMessageBatchMaxSize(3);

        if (isOrder) {
            consumer.registerMessageListener(orderMessageListener);
        } else {
            consumer.registerMessageListener(concurrentMessageListener);
        }

        consumer.start();
        log.info("开始消费消息");
    }

    static MessageListenerConcurrently concurrentMessageListener = (msgList, context) -> {
        log.info("concurrent ... ");
        msgList.forEach(msg -> log.info("{} < {}", new String(msg.getBody()), context.getMessageQueue()));

        /**
         * 并行消费 ConsumeConcurrentlyStatus.RECONSUME_LATER
         * messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
         * 默认重新投递16次 从10s开始 18个等级-2
         * 如果设置了context.setDelayLevelWhenNextConsume(2) 就不会使用默认策略 每次投递都间隔这次等级 总次数计算逻辑不变
         */

        context.setDelayLevelWhenNextConsume(2);
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    };

    static MessageListenerOrderly orderMessageListener = (msgList, context) -> {
        log.info("order ... ");
        msgList.forEach(msg -> log.info("{} < {}", new String(msg.getBody()), context.getMessageQueue()));
        /**
         * 顺序消费 ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT
         * 默认重新投递无限次 直到消费成功 因为顺序消费 会阻塞后面的消息
         * context.setSuspendCurrentQueueTimeMillis(TimeUnit.SECONDS.toMillis(10)) 设置的间隔时间优先级高于 consumer.setSuspendCurrentQueueTimeMillis(TimeUnit.SECONDS.toMillis(5))
         */
        return ConsumeOrderlyStatus.SUCCESS;
    };

    @SneakyThrows
    @Test
    void pullConsumer() {
        DefaultLitePullConsumer consumer = new DefaultLitePullConsumer(DEF_GROUP);
        consumer.setNamesrvAddr(NAME_SERVER);
        consumer.subscribe(DEF_TOPIC, "*");
        consumer.start();
        log.info("开始消费消息");

        // 长轮询 从ProcessQueue获取
        List<MessageExt> msgList = consumer.poll();

        msgList.forEach(msg -> log.info("{} | {}", new String(msg.getBody()), msg.getReconsumeTimes()));

        System.in.read();
    }

}
