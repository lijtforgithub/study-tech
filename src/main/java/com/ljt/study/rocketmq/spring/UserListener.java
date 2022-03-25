package com.ljt.study.rocketmq.spring;

import com.ljt.study.rocketmq.core.AbstractRocketMQListener;
import com.ljt.study.rocketmq.core.MessageContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author LiJingTang
 * @date 2021-07-07 09:41
 */
@Slf4j
@Component
//@RocketMQMessageListener(topic = "${rocketmq.test.topic}", consumerGroup = "${rocketmq.test.group}", consumeMode = ConsumeMode.ORDERLY)
class UserListener extends AbstractRocketMQListener<TempUser> {

    public UserListener() {
        super(new MessageContext().setConsumerGroup("test_spring_group"));
    }

    @Override
    public void handleMessage(TempUser msg) {
        log.info("处理消息：{}", msg);
        Assert.isTrue( 1 != 1, "XXOO");
    }

}