package com.ljt.study.mqtt.integration;

import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * @author LiJingTang
 * @date 2022-06-29 10:27
 */
@SpringJUnitConfig({MqttConfig.class, MqttProducerConfig.class})
class MqttIntegrationTest {

    @Autowired
    private MqttGateway mqttGateway;

    @Test
    @SneakyThrows
    void send() {
        mqttGateway.sendToMqtt("test/ox/xo", RandomStringUtils.randomAlphanumeric(10));
    }

}
