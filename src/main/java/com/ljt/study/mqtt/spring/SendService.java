package com.ljt.study.mqtt.spring;

/**
 * @author LiJingTang
 * @date 2022-06-30 14:52
 */
public interface SendService {

    void send(final String topic, final String context);

}
