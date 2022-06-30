package com.ljt.study.mqtt.spring.v5;

import com.ljt.study.mqtt.MqttConfigProperties;
import com.ljt.study.mqtt.spring.AbstractClientService;
import com.ljt.study.mqtt.spring.SendService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * @author LiJingTang
 * @date 2022-06-30 9:32
 */
@Slf4j(topic = "mqtt-v5客户端")
@Getter
final class MqttClientService extends AbstractClientService implements MqttCallback, SendService {


    @Autowired
    private MqttClient mqttClient;
    @Autowired
    private MqttConnectionOptions options;
    @Autowired
    private MqttConfigProperties mqttConfigProperties;

    @PostConstruct
    void init() {
        mqttClient.setCallback(this);
        connectServer();
    }

    @Override
    protected void connect() throws MqttException {
        IMqttToken mqttToken = mqttClient.connectWithResult(options);
        mqttToken.waitForCompletion();
        log.info("mqtt连接成功\n{}", options);

    }

    @Override
    protected String getClientId() {
        return mqttClient.getClientId();
    }

    @Override
    protected void disconnect() throws MqttException {
        if (!mqttClient.isConnected()) {
            mqttClient.disconnect();
        }
    }

    @Override
    protected boolean isConnected() {
        return mqttClient.isConnected();
    }

    @Override
    public void send(final String topic, final String context) {
        MqttMessage message = new MqttMessage();
        message.setPayload(context.getBytes(StandardCharsets.UTF_8));
        message.setQos(1);
        message.setRetained(false);

        MqttTopic mqttTopic = mqttClient.getTopic(topic);

        try {
            MqttToken token = mqttTopic.publish(message);
            token.waitForCompletion();
            log.info("发送消息成功 {}", context);
        } catch (MqttException e) {
            log.info("发送消息失败 " + context, e);
        }
    }

    void subscribe() {
        try {
            mqttClient.subscribe(mqttConfigProperties.getTopic(), 1
                    //                , (topic, message) -> log.info("Listener收到消息 {} => {}", topic, new String(message.getPayload()))
            );

            log.info("订阅主题成功 {}", mqttConfigProperties.getTopic());
        } catch (MqttException e) {
            log.error("订阅主题失败", e);
        }
    }


    @Override
    public void mqttErrorOccurred(MqttException exception) {

    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {

    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        log.info("是否重连{} {}", serverURI, reconnect);
        renew();

        if (isSubscribe()) {
            subscribe();
        }
    }

    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse) {
        super.disconnected();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        log.info("Callback收到消息 {} => {}", topic, new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttToken token) {
        log.info("消息发送成功 {}", token.getMessageId());
    }

}
