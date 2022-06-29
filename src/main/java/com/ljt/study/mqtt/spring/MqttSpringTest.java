package com.ljt.study.mqtt.spring;

import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2022-06-29 14:27
 */
@SpringBootTest
class MqttSpringTest {

    @Autowired
    private MqttClient mqttClient;

    @SneakyThrows
    @Test
    void send() {
        MqttMessage message = new MqttMessage();
        message.setPayload(RandomStringUtils.randomAlphanumeric(5).getBytes(StandardCharsets.UTF_8));
        message.setQos(1);
        message.setRetained(false);

        MqttTopic topic = mqttClient.getTopic("test/xxoo");
        MqttDeliveryToken token = topic.publish(message);
        token.waitForCompletion();

        TimeUnit.SECONDS.sleep(10);
    }

}
