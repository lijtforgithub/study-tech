package com.ljt.study.rabbitmq.client;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2020-01-16 11:31
 */
public class RoutingTest {

    public static void main(String[] args) throws Exception {
        Producer.send();
        Consumer.receive();

        TimeUnit.SECONDS.sleep(5);
        RabbitMQUtils.closeConnection();
    }


    private static final String[] QUEUE = {"test.client.log.debug", "test.client.log.info", "test.client.log.error"};
    private static final String[] ROUTING_KEY = {"key.log.debug", "key.log.info", "key.log.error"};

    private static class Producer {

        static void send() throws Exception {
            try (Channel channel = RabbitMQUtils.getChannel()) {
                if (Objects.isNull(channel)) {
                    System.out.println("Channel is null.");
                    System.exit(-1);
                }

                channel.exchangeDeclare(RabbitMQUtils.getDirectExchangeName(), BuiltinExchangeType.DIRECT);

                for (int i = 0; i < 3; i++) {
                    channel.queueDeclare(QUEUE[i], false, false, false, null);
                }

                for (int i = 0; i < 3; i++) {
                    channel.queueBind(QUEUE[0], RabbitMQUtils.getDirectExchangeName(), ROUTING_KEY[i]);
                }
                for (int i = 1; i < 3; i++) {
                    channel.queueBind(QUEUE[1], RabbitMQUtils.getDirectExchangeName(), ROUTING_KEY[i]);
                }

                channel.queueBind(QUEUE[2], RabbitMQUtils.getDirectExchangeName(), ROUTING_KEY[2]);
                String[] messages = {"debug.log", "info.log", "error.log"};

                for (int i = 0; i < 3; i++) {
                    channel.basicPublish(RabbitMQUtils.getDirectExchangeName(), ROUTING_KEY[i], null, messages[i].getBytes());
                    System.out.println("Sent " + messages[i]);
                }
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

            channel.exchangeDeclare(RabbitMQUtils.getDirectExchangeName(), BuiltinExchangeType.DIRECT);
            System.out.println("Waiting for messages ...");

            for (int i = 0; i < 3; i++) {
                final String queueName = QUEUE[i];
                channel.basicConsume(queueName, true, new DefaultConsumer(channel) {

                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                        String message = new String(body, StandardCharsets.UTF_8);
                        System.out.println("Received " + queueName + "(" + envelope.getRoutingKey() + ") : " + message);
                    }
                });
            }
        }
    }

}
