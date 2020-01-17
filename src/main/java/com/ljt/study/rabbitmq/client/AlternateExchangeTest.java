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
        new Producer().send();
        new Consumer().receive();

        TimeUnit.SECONDS.sleep(20);
        RabbitMQUtils.closeConnection();
    }

    private static final String AE_QUEUE = "test.client.ae.unrouted";

    private static class Producer {

        private static final String KEY = "alternate-exchange";
        private static final String DIRECT_EXCHANGE = "test.direct.ae";
        private static final String FANOUT_EXCHANGE = "test.fanout.ae";

        private static final String QUEUE = "test.client.ae.normal";

        static void send() throws Exception {
            try (Channel channel = RabbitMQUtils.getChannel()) {
                Map<String, Object> args = Maps.newHashMapWithExpectedSize(1);
                args.put(KEY, FANOUT_EXCHANGE);

                channel.exchangeDeclare(DIRECT_EXCHANGE, BuiltinExchangeType.DIRECT, false, true, args);
                channel.queueDeclare(QUEUE, false, true, true, null);
                channel.queueBind(QUEUE, DIRECT_EXCHANGE, QUEUE);

                channel.exchangeDeclare(FANOUT_EXCHANGE, BuiltinExchangeType.FANOUT, false);
                channel.queueDeclare(AE_QUEUE, false, true, true, null);
                channel.queueBind(AE_QUEUE, FANOUT_EXCHANGE, "");

                String message = "备用交换机";
                channel.basicPublish(DIRECT_EXCHANGE, "unbind-key", null, message.getBytes());
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

            channel.basicConsume(AE_QUEUE, true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Received " + message + " | RoutingKey = " + delivery.getEnvelope().getRoutingKey());
            }, consumerTag -> {
            });
        }
    }

}
