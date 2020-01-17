package com.ljt.study.rabbitmq.client;

import com.google.common.collect.Maps;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2020-01-17 15:37
 */
public class AlternateExchangeTest {

    public static void main(String[] args) throws Exception {
        Producer.send();
        Consumer.receive();

        TimeUnit.SECONDS.sleep(20);
        RabbitMQUtils.closeConnection();
    }

    private static final String QUEUE_AE = "test.client.ae.unrouted";

    private static class Producer {

        private static final String KEY = "alternate-exchange";
        private static final String EXCHANGE_DIRECT = "test.direct.ae";
        private static final String EXCHANGE_FANOUT = "test.fanout.ae";

        private static final String QUEUE = "test.client.ae.normal";

        static void send() throws Exception {
            try (Channel channel = RabbitMQUtils.getChannel()) {
                if (Objects.isNull(channel)) {
                    System.out.println("Channel is null.");
                    System.exit(-1);
                }

                Map<String, Object> args = Maps.newHashMapWithExpectedSize(1);
                args.put(KEY, EXCHANGE_FANOUT);

                channel.exchangeDeclare(EXCHANGE_DIRECT, BuiltinExchangeType.DIRECT, false, true, args);
                channel.queueDeclare(QUEUE, false, true, true, null);
                channel.queueBind(QUEUE, EXCHANGE_DIRECT, QUEUE);

                channel.exchangeDeclare(EXCHANGE_FANOUT, BuiltinExchangeType.FANOUT, false);
                channel.queueDeclare(QUEUE_AE, false, true, true, null);
                channel.queueBind(QUEUE_AE, EXCHANGE_FANOUT, "");

                String message = "备用交换机";
                channel.basicPublish(EXCHANGE_DIRECT, "unbind-key", null, message.getBytes());
            }
        }
    }

    private static class Consumer {

        static void receive() throws IOException {
            Channel channel = RabbitMQUtils.getChannel();

            if (Objects.isNull(channel)) {
                System.out.println("Channel is null.");
                System.exit(-1);
            }

            channel.basicConsume(QUEUE_AE, true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Received " + message + " | RoutingKey = " + delivery.getEnvelope().getRoutingKey());
            }, consumerTag -> {
            });
        }
    }

}
