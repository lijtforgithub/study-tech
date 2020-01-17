package com.ljt.study.rabbitmq.client;


import com.google.common.collect.Maps;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2020-01-17 16:32
 */
public class TTLTest {

    public static void main(String[] args) throws Exception {
        TTLQueue.send();
        TTLMessage.send();

        TimeUnit.SECONDS.sleep(30);
        RabbitMQUtils.closeConnection();
    }


    private static final String KEY = "x-message-ttl";
    private static final String QUEUE_TTL = "test.client.ttl";
    private static final String QUEUE_TTL_MSG = "test.client.ttl.message";

    private static class TTLQueue {

        static void send() throws Exception {
            try (Channel channel = RabbitMQUtils.getChannel()) {
                Map<String, Object> args = Maps.newHashMapWithExpectedSize(1);
                args.put(KEY, 10000);

                channel.queueDeclare(QUEUE_TTL, false, true, true, args);
                String message = "Hello TTL queue";
                channel.basicPublish(RabbitMQUtils.getDefaultExchangeName(), QUEUE_TTL, null, message.getBytes());
            }
        }
    }

    private static class TTLMessage {

        static void send() throws Exception {
            try (Channel channel = RabbitMQUtils.getChannel()) {
                channel.queueDeclare(QUEUE_TTL_MSG, true, false, false, null);
                String message = "Hello TTL message";
                AMQP.BasicProperties properties =
                        new AMQP.BasicProperties().builder().deliveryMode(2).expiration("10000").build();

                channel.basicPublish(RabbitMQUtils.getDefaultExchangeName(), QUEUE_TTL_MSG, properties,
                        message.getBytes());
            }
        }
    }

}
