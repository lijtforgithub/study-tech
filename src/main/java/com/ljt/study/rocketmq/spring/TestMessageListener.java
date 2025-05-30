package com.ljt.study.rocketmq.spring;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2025-02-20 09:06
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "test", topic = "test_client_topic", consumeMode = ConsumeMode.ORDERLY)
public class TestMessageListener implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt messageExt) {
        byte[] body = messageExt.getBody();
        String content = new String(body);
        log.info("队列={} 内容={}", messageExt.getQueueId(), content);

        if ("03".equals(content)) {
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
//            throw new RuntimeException("ERROR");
        }

    }

}
