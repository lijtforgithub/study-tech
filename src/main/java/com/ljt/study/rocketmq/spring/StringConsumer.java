package com.ljt.study.rocketmq.spring;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author LiJingTang
 * @date 2021-07-07 09:41
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "${rocketmq.test.topic}", consumerGroup = "${rocketmq.test.group}")
class StringConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.info("收到消息：{}", message);
    }

}