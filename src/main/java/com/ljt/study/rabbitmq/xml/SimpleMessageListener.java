package com.ljt.study.rabbitmq.xml;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

/**
 * @author LiJingTang
 * @date 2020-01-17 10:34
 */
public class SimpleMessageListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        String queue = message.getMessageProperties().getReceivedRoutingKey();
        String msg = new String(message.getBody());

        System.out.println(queue + " - " + msg);
    }

}
