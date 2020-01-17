package com.ljt.study.rabbitmq.client;

import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 事务
 *
 * @author LiJingTang
 * @date 2020-01-17 09:20
 */
public class TxTest {

    public static void main(String[] args) throws Exception {
        new Producer().send();
        new Consumer().receive();

        TimeUnit.SECONDS.sleep(20);
        RabbitMQUtils.closeConnection();
        System.out.println("消息消费完 连接关闭 队列自动删除了");
    }


    private static final String QUEUE = "test.client.tx";

    private static class Producer {

        static void send() throws Exception {
            Channel channel = RabbitMQUtils.getChannel();

            if (Objects.isNull(channel)) {
                System.out.println("Channel is null.");
                System.exit(-1);
            }

            try {

                channel.queueDeclare(QUEUE, false, false, true, null);
                String message = "Hello World";

                // 声明事务
                channel.txSelect();
                channel.basicPublish(RabbitMQUtils.getDefaultExchangeName(), QUEUE, null, message.getBytes());

                if ((System.currentTimeMillis() & 1) == 0) {
                    throw new UnsupportedOperationException();
                }
                channel.txCommit();

                System.out.println("提交事务");
            } catch (Exception e) {
                channel.txRollback();
                System.out.println("回滚事务");
            } finally {
                channel.close();
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

            channel.basicConsume(QUEUE, true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Received " + message);
            }, consumerTag -> {
            });
        }
    }

}
