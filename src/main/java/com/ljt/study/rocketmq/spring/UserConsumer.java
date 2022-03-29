package com.ljt.study.rocketmq.spring;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

/**
 * @author jtli3
 * @date 2022-03-25 16:55
 */
@Slf4j
//@Component
class UserConsumer {

    @Value("${rocketmq.test.topic}")
    private String topic;
    @Value("${rocketmq.test.group}")
    private String group;
    @Autowired
    private RocketMQProperties rocketMQProperties;
    @Autowired
    private MessageListenerOrderly messageListener;

    @PostConstruct
    public void init() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setNamesrvAddr(rocketMQProperties.getNameServer());
        consumer.subscribe(topic, "*");

        consumer.registerMessageListener(messageListener);

        consumer.start();
        log.info("开始消费消息");
    }

}
