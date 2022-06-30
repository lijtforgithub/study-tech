package com.ljt.study.mqtt;

import com.ljt.study.YamlPropertySourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import java.nio.charset.StandardCharsets;

/**
 * @author LiJingTang
 * @date 2022-06-29 13:35
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(MqttConfigProperties.class)
@PropertySource(value = "classpath:mqtt/application.yml", factory = YamlPropertySourceFactory.class)
public class MqttCommonConfig {

    @Bean
    @Profile(MqttVersion.V3)
    MqttConnectOptions mqttV3Options(MqttConfigProperties configProperties) {
        MqttConnectOptions options = new MqttConnectOptions();
        // 断开自动重连
//        options.setAutomaticReconnect(true);
        options.setUserName(configProperties.getUserName());
        options.setPassword(configProperties.getPassword().toCharArray());
        options.setServerURIs(new String[]{configProperties.getServerUri()});

        return options;
    }

    @Bean
    @Profile(MqttVersion.V5)
    MqttConnectionOptions mqttV5Options(MqttConfigProperties configProperties) {
        MqttConnectionOptions options = new MqttConnectionOptions();
        // 断开自动重连
//        options.setAutomaticReconnect(true);
        options.setUserName(configProperties.getUserName());
        options.setPassword(configProperties.getPassword().getBytes(StandardCharsets.UTF_8));
        options.setServerURIs(new String[]{configProperties.getServerUri()});

        return options;
    }

}
