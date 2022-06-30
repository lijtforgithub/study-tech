package com.ljt.study.mqtt.spring;

import com.ljt.study.mqtt.MqttVersion;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author LiJingTang
 * @date 2022-06-29 14:27
 */
@ActiveProfiles({MqttVersion.ONLY_SEND, MqttVersion.V3})
@SpringBootTest
class MqttSpringTest {

    @Autowired
    private SendService sendService;

    @Test
    void send() throws InterruptedException {
        sendService.send("xxoo", RandomStringUtils.randomAlphanumeric(5));
    }

}
