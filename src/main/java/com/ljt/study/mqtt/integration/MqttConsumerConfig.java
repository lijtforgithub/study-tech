package com.ljt.study.mqtt.integration;

import com.ljt.study.mqtt.MqttConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import static com.ljt.study.mqtt.integration.MqttConfig.R;

/**
 * @author LiJingTang
 * @date 2022-06-29 11:08
 */
@Slf4j
@Configuration
class MqttConsumerConfig {

    @Autowired
    private MqttConfigProperties mqttConfigProperties;

    @Bean
    public MessageProducer inbound(MqttPahoClientFactory mqttClientFactory) {
        log.info("MqttPahoMessageDrivenChannelAdapter");
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("producer_" + R.nextInt(), mqttClientFactory, mqttConfigProperties.getTopic());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setOutputChannel(mqttInputChannel());
        adapter.setRecoveryInterval(1000);
        adapter.setCompletionTimeout(5000);
        adapter.setQos(1);
        return adapter;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> log.info("收到消息 {}", message.getPayload());
    }

}
