package com.ljt.study.rabbitmq.client;


import com.rabbitmq.client.Channel;

/**
 * @author LiJingTang
 * @date 2020-01-17 16:32
 */
public class TTLTest {


    private static final String KEY = "x-message-ttl";
    private static final String TTL_QUEUE = "x-message-ttl";

    private static class TTLQueue {

        static void send() throws Exception {
            try (Channel channel = RabbitMQUtils.getChannel()) {
                channel.queueDeclare();
            }
        }
    }

}
