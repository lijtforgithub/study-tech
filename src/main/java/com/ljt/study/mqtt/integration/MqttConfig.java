package com.ljt.study.mqtt.integration;

import com.ljt.study.mqtt.MqttCommonConfig;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

import java.util.Random;

/**
 * @author LiJingTang
 * @date 2022-06-29 10:12
 */
@Import(MqttCommonConfig.class)
@Configuration
@IntegrationComponentScan
class MqttConfig {

    @Autowired
    private MqttConnectOptions mqttConnectOptions;

    static final Random R = new Random();

    @Bean
    MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions);
        return factory;
    }

}
