package com.ljt.study.mqtt.spring;

import com.ljt.study.mqtt.MqttCommonConfig;
import com.ljt.study.mqtt.MqttConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author LiJingTang
 * @date 2022-06-29 14:03
 */
@Slf4j
@Import(MqttCommonConfig.class)
@Configuration
class MqttConfig {

    @Autowired
    private MqttConfigProperties mqttConfigProperties;
    @Autowired
    private MqttConnectOptions mqttConnectOptions;
    @Autowired
    private CustomMqttCallback customMqttCallback;

    @Bean
    MqttClient mqttClient() throws MqttException {
        MqttClient client = new MqttClient(mqttConfigProperties.getServerUri(), RandomStringUtils.randomAlphabetic(10), new MemoryPersistence());
        client.setCallback(customMqttCallback);
        IMqttToken mqttToken = client.connectWithResult(mqttConnectOptions);
        mqttToken.waitForCompletion();

        return client;
    }

}
