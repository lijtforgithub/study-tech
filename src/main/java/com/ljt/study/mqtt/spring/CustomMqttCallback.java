package com.ljt.study.mqtt.spring;

import com.ljt.study.mqtt.MqttConfigProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author LiJingTang
 * @date 2022-06-29 14:18
 */
@Slf4j
@Component
class CustomMqttCallback implements MqttCallbackExtended {

    @Autowired
    private MqttConfigProperties mqttConfigProperties;
    @Autowired
    private MqttClient client;

    @Override
    public void connectionLost(Throwable cause) {
        log.info("连接断开 {}", cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        log.info("Callback收到消息 {} => {}", topic, new String(message.getPayload()));
    }

    @SneakyThrows
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.info("消息发送成功 {}", token.getMessageId());
    }

    @SneakyThrows
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        client.subscribe(mqttConfigProperties.getTopic(), 1
//                , (topic, message) -> log.info("Listener收到消息 {} => {}", topic, new String(message.getPayload()))
        );

        log.info("{} 订阅 {}", reconnect, mqttConfigProperties.getTopic());
    }

}
