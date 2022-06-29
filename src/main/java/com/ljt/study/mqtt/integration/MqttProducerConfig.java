package com.ljt.study.mqtt.integration;

import com.ljt.study.mqtt.MqttConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import static com.ljt.study.mqtt.integration.MqttConfig.R;

/**
 * @author LiJingTang
 * @date 2022-06-29 11:08
 */
@Configuration
class MqttProducerConfig {

    @Autowired
    private MqttPahoClientFactory mqttClientFactory;
    @Autowired
    private MqttConfigProperties mqttConfigProperties;

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("consumer_" + R.nextInt(), mqttClientFactory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(mqttConfigProperties.getTopic());
        return messageHandler;
    }

    @Bean
    MessageChannel mqttOutboundChannel() {
        DirectChannel channel = new DirectChannel();
        channel.subscribe(mqttOutbound());
        return channel;
    }

}
