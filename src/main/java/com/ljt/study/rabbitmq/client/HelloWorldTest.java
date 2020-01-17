package com.ljt.study.rabbitmq.client;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2020-01-16 11:04
 */
public class HelloWorldTest {

    public static void main(String[] args) throws Exception {
        new Producer().send();
        new Consumer().receive();

        TimeUnit.SECONDS.sleep(20);
        RabbitMQUtils.closeConnection();
    }


    private static final String QUEUE = "test.client.helloworld";

    private static final boolean durable = false; // 持久化 服务器重启后队列还在
    private static final boolean exclusive = true; // 独占队列 仅限于此连接 连接关闭自动删除（有消息也会）
    private static final boolean autoDelete = false;  // 自动删除队列 服务器不再使用是自动删除（消息消费完）

    private static class Producer {

        static void send() throws Exception {
            try (Channel channel = RabbitMQUtils.getChannel()) {
                if (Objects.isNull(channel)) {
                    System.out.println("Channel is null.");
                    System.exit(-1);
                }

//                channel.queueDeclare(); 默认值 false true true
                channel.queueDeclare(QUEUE, durable, exclusive, autoDelete, null);
                String message = "Hello World!";

                channel.basicPublish(RabbitMQUtils.getDefaultExchangeName(), QUEUE, null, message.getBytes());
                System.out.println("Sent " + message);
            }
        }
    }

    private static class Consumer {

        static void receive() throws Exception {
            Channel channel = RabbitMQUtils.getChannel(); // 不能关闭 消息处理是异步

            if (Objects.isNull(channel)) {
                System.out.println("Channel is null.");
                System.exit(-1);
            }

            channel.queueDeclare(QUEUE, durable, exclusive, autoDelete, null);
            System.out.println("Waiting for messages ...");

            channel.basicConsume(QUEUE, true, new DefaultConsumer(channel) {

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    String message = new String(body, StandardCharsets.UTF_8);
                    System.out.println("Received " + message);
                }
            });
        }
    }

}
