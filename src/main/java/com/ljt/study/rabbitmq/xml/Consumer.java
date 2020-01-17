package com.ljt.study.rabbitmq.xml;

/**
 * @author LiJingTang
 * @date 2020-01-17 10:00
 */
public class Consumer {

    public void handleMessage(String message) {
        System.out.println("Received: " + message);
    }

}
