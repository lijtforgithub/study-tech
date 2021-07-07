package com.ljt.study.rocketmq.spring;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author LiJingTang
 * @date 2021-07-07 09:41
 */
@Slf4j
@SpringBootTest
class BootTest {

    @Value("${rocketmq.test.group}")
    private String testTopic;
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Test
    void syncSend() {
        rocketMqTemplate.syncSend(testTopic, "rocketMQTemplate:syncSend");
    }

}
