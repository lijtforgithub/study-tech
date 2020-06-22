package com.ljt.study.rabbitmq.client;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2020-01-16 12:17
 */
public class SubscribeTest {

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

                channel.exchangeDeclare(RabbitMQUtils.getFanoutExchangeName(), BuiltinExchangeType.FANOUT);
                String queueName = channel.queueDeclare().getQueue();
                channel.queueBind(queueName, RabbitMQUtils.getFanoutExchangeName(), "");

                String message = "Hello World!fanout";
                channel.basicPublish(RabbitMQUtils.getFanoutExchangeName(), "", null, message.getBytes());
                System.out.println("Sent " + message);
            }
        }
    }

    private static class Consumer {

        private static final String QUEUE = "test.client.subscribe";

        static void receive() throws IOException {
            Channel channel = RabbitMQUtils.getChannel();
            if (Objects.isNull(channel)) {
                System.out.println("Channel is null.");
                System.exit(-1);
            }

            channel.exchangeDeclare(RabbitMQUtils.getFanoutExchangeName(), BuiltinExchangeType.FANOUT);
            channel.queueDeclare(QUEUE, false, false, false, null);
            channel.queueBind(QUEUE, RabbitMQUtils.getFanoutExchangeName(), "");
            System.out.println("Waiting for messages ...");

            channel.basicConsume(QUEUE, true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Received " + message);
            }, consumerTag -> {
            });
        }
    }

}
