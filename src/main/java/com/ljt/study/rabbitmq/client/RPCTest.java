package com.ljt.study.rabbitmq.client;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2020-01-19 09:28
 */
public class RPCTest {

    public static void main(String[] args) throws Exception {
        RPCClient.call();
        RPCServer.start();

        TimeUnit.SECONDS.sleep(30);
        RabbitMQUtils.closeConnection();
    }


    private static final String QUEUE = "test.client.rpc";

    private static class RPCClient {

        static void call() throws Exception {
            Channel channel = RabbitMQUtils.getChannel();
            if (Objects.isNull(channel)) {
                System.out.println("Channel is null.");
                System.exit(-1);
            }

            channel.queueDeclare(QUEUE, false, false, false, null);
            String call = channel.queueDeclare().getQueue();
            String correlationId = UUID.randomUUID().toString();

            AMQP.BasicProperties prop =
                    new AMQP.BasicProperties().builder().replyTo(call).correlationId(correlationId).build();

            String message = "PRC Test";
            channel.basicPublish(RabbitMQUtils.getDefaultExchangeName(), QUEUE, prop, message.getBytes());

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    if (correlationId.equals(properties.getCorrelationId())) {
                        System.out.println(message + " => " + new String(body, StandardCharsets.UTF_8));
                    }
                }
            };

            channel.basicConsume(call, true, consumer);
        }
    }

    private static class RPCServer {

        static void start() throws IOException {
            Channel channel = RabbitMQUtils.getChannel();
            if (Objects.isNull(channel)) {
                System.out.println("Channel is null.");
                System.exit(-1);
            }

            channel.basicQos(1);
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    AMQP.BasicProperties replyProp =
                            new AMQP.BasicProperties().builder().correlationId(properties.getCorrelationId()).build();
                    try {
                        String message = new String(body, StandardCharsets.UTF_8);
                        System.out.println("接收到消息：" + message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        String response = ".1.1";
                        channel.basicPublish(RabbitMQUtils.getDefaultExchangeName(), properties.getReplyTo(),
                                replyProp, response.getBytes());
                        System.out.println("回复消息：" + response);
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                }
            };

            channel.basicConsume(QUEUE, false, consumer);
        }
    }

}
