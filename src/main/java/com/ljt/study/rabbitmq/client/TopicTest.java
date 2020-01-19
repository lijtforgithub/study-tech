package com.ljt.study.rabbitmq.client;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2020-01-16 12:15
 */
public class TopicTest {

    public static void main(String[] args) throws Exception {
        Consumer.receive();
        Producer.send();

        TimeUnit.SECONDS.sleep(5);
        RabbitMQUtils.closeConnection();
    }

    private static class Producer {

        static void send() throws Exception {
            try (Channel channel = RabbitMQUtils.getChannel()) {
                if (Objects.isNull(channel)) {
                    System.out.println("Channel is null.");
                    System.exit(-1);
                }

                channel.exchangeDeclare(RabbitMQUtils.getTopicExchangeName(), BuiltinExchangeType.TOPIC);

                String message = "topic message";
                channel.basicPublish(RabbitMQUtils.getTopicExchangeName(), "key.log.topic", null, message.getBytes());
                System.out.println("Sent " + message);
            }
        }
    }

    private static class Consumer {

        private static final String TOPIC_ROUTING_KEY = "key.log.*";

        static void receive() throws IOException {
            Channel channel = RabbitMQUtils.getChannel();
            if (Objects.isNull(channel)) {
                System.out.println("Channel is null.");
                System.exit(-1);
            }

            channel.exchangeDeclare(RabbitMQUtils.getTopicExchangeName(), BuiltinExchangeType.TOPIC);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, RabbitMQUtils.getTopicExchangeName(), TOPIC_ROUTING_KEY);
            System.out.println("Waiting for messages ...");

            channel.basicConsume(queueName, true, new DefaultConsumer(channel) {

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    String message = new String(body, StandardCharsets.UTF_8);
                    System.out.println("Received " + envelope.getRoutingKey() + " : " + message);
                }
            });
        }
    }

}
