package com.ljt.study.mqtt.spring.v3;

import com.ljt.study.mqtt.MqttCommonConfig;
import com.ljt.study.mqtt.MqttConfigProperties;
import com.ljt.study.mqtt.MqttVersion;
import com.ljt.study.mqtt.spring.SendService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * @author LiJingTang
 * @date 2022-06-29 14:03
 */
@Slf4j
@Profile(MqttVersion.V3)
@Configuration
@Import(MqttCommonConfig.class)
class MqttConfig {

    @Autowired
    private MqttConfigProperties mqttConfigProperties;

    @Bean
    MqttClient mqttClient() throws MqttException {
        return new MqttClient(mqttConfigProperties.getServerUri(), RandomStringUtils.randomAlphabetic(10), new MemoryPersistence());
    }

    @Bean
    SendService sendService() {
        return new MqttClientService();
    }

}
