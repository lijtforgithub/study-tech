package com.ljt.study.mqtt;

import com.ljt.study.YamlPropertySourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

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
    MqttConnectOptions mqttConnectOptions(MqttConfigProperties configProperties) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setUserName(configProperties.getUserName());
        options.setPassword(configProperties.getPassword().toCharArray());
        options.setServerURIs(new String[]{configProperties.getServerUri()});

        return options;
    }

}
