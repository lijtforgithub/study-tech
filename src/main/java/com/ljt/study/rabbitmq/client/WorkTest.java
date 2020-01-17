package com.ljt.study.rabbitmq.client;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2020-01-16 12:21
 */
public class WorkTest {

    public static void main(String[] args) throws Exception {
        MSG_LIST.add(new String[]{"First message."});
        MSG_LIST.add(new String[]{"Second message.."});
        MSG_LIST.add(new String[]{"Third message..."});

        new Producer().send();
        new Consumer().receive();

        TimeUnit.SECONDS.sleep(10);
        RabbitMQUtils.closeConnection();
    }


    private static final String QUEUE = "test.client.work";
    private static final List<String[]> MSG_LIST = new ArrayList<>(3);

    private static class Producer {

        static void send() throws Exception {
            try (Channel channel = RabbitMQUtils.getChannel()) {

                if (Objects.isNull(channel)) {
                    System.out.println("Channel is null.");
                    System.exit(-1);
                }

                channel.queueDeclare(QUEUE, true, false, false, null);

                for (String[] array : MSG_LIST) {
                    String message = getMessage(array);
                    // 将队列设置为持久化之后，还需要将消息也设为可持久化的，MessageProperties.PERSISTENT_TEXT_PLAIN
                    channel.basicPublish(RabbitMQUtils.getDefaultExchangeName(), QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
                    System.out.println("Sent " + message);
                }
            }
        }

        private static String getMessage(String[] strings) {
            if (strings.length < 1)
                return "Hello World!";

            return String.join(" ", strings);
        }
    }

    private static class Consumer {

        static void receive() throws IOException {
            Channel channel = RabbitMQUtils.getChannel();

            if (Objects.isNull(channel)) {
                System.out.println("Channel is null.");
                System.exit(-1);
            }

            channel.queueDeclare(QUEUE, true, false, false, null);
            System.out.println("Waiting for messages ...");

            int prefetchCount = 1;
            channel.basicQos(prefetchCount);
            boolean autoAck = false;

            channel.basicConsume(QUEUE, autoAck, new DefaultConsumer(channel) {

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);
                    System.out.println("Received " + message);

                    try {
                        doWork(message);
                    } finally {
                        System.out.println("Done");
                        channel.basicAck(envelope.getDeliveryTag(), false); // 消息确认
                    }
                }
            });
        }

        private static void doWork(String task) {
            try {
                for (char ch : task.toCharArray()) {
                    if (ch == '.') {
                        Thread.sleep(1000);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
