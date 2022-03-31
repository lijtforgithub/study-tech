package com.ljt.study.rocketmq.core;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author jtli3
 * @date 2022-03-25 10:42
 */
@Slf4j
@Setter
class RepeatConsumeProcessor implements MessageProcessor {

    private String prefix;
    private Long cacheTime;
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean beforeHandle(MessageExt message, MessageContext context) {
        if (MessageModel.BROADCASTING == context.getMessageModel()) {
            return true;
        }

        log.debug("开始消息重复消费验证topic={} group={} msgId={}", message.getTopic(), context.getConsumerGroup(), message.getMsgId());
        String key = getKey(message, context);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            log.warn("重复投递{}", key);
            return false;
        }

        return true;
    }

    @Override
    public void afterHandle(MessageExt message, MessageContext context) {
        if (MessageModel.BROADCASTING == context.getMessageModel()) {
            return;
        }

        String key = getKey(message, context);
        stringRedisTemplate.opsForValue().set(key, "1", cacheTime, TimeUnit.SECONDS);
    }

    private String getKey(MessageExt message, MessageContext context) {
        return String.format("%s:%s:%s:%s", prefix, message.getTopic(), context.getConsumerGroup(), message.getMsgId());
    }

}
