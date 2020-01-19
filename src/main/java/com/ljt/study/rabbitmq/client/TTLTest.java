package com.ljt.study.rabbitmq.client;


import com.google.common.collect.Maps;
import com.ljt.study.rabbitmq.delay.DelayConfig;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2020-01-17 16:32
 */
public class TTLTest {

    public static void main(String[] args) throws Exception {
        TTLQueue.send();
        TTLMessage.send();

        TTLQueueX.declare();

        TimeUnit.SECONDS.sleep(30);
        RabbitMQUtils.closeConnection();
    }


    /**
     * 队列上设置消息过期时间
     */
    private static class TTLQueue {

        private static final String QUEUE = "test.client.ttl";

        static void send() throws Exception {
            try (Channel channel = RabbitMQUtils.getChannel()) {
                if (Objects.isNull(channel)) {
                    System.out.println("Channel is null.");
                    System.exit(-1);
                }

                Map<String, Object> args = Maps.newHashMapWithExpectedSize(1);
                args.put(DelayConfig.KEY_TTL, 10000);

                channel.queueDeclare(QUEUE, false, true, true, args);
                String message = "Hello TTL queue";
                channel.basicPublish(RabbitMQUtils.getDefaultExchangeName(), QUEUE, null, message.getBytes());
            }
        }
    }

    /**
     * 消息设置过期时间
     */
    private static class TTLMessage {

        private static final String QUEUE = "test.client.ttl.message";

        static void send() throws Exception {
            try (Channel channel = RabbitMQUtils.getChannel()) {
                if (Objects.isNull(channel)) {
                    System.out.println("Channel is null.");
                    System.exit(-1);
                }

                channel.queueDeclare(QUEUE, false, false, false, null);
                String message = "Hello TTL message";
                AMQP.BasicProperties prop =
                        new AMQP.BasicProperties().builder().deliveryMode(2).expiration("10000").build();

                channel.basicPublish(RabbitMQUtils.getDefaultExchangeName(), QUEUE, prop,
                        message.getBytes());
            }
        }
    }

    /**
     * 队列过期时间
     */
    private static class TTLQueueX {

        private static final String KEY = "x-expires";
        private static final String QUEUE = "test.client.ttl.temp";

        static void declare() throws Exception {
            try (Channel channel = RabbitMQUtils.getChannel()) {
                if (Objects.isNull(channel)) {
                    System.out.println("Channel is null.");
                    System.exit(-1);
                }

                Map<String, Object> args = Maps.newHashMapWithExpectedSize(1);
                args.put(KEY, 10000);

                channel.queueDeclare(QUEUE, true, false, false, args);
            }
        }
    }

}
