package com.ljt.study.rabbitmq.client;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

/**
 * 发送确认和消费确认
 *
 * @author LiJingTang
 * @date 2020-01-16 15:05
 */
public class AckTest {

    public static void main(String[] args) throws Exception {
//        new Producer().send();
        new Consumer().receive();

        TimeUnit.SECONDS.sleep(5);
        RabbitMQUtils.closeConnection();
    }


    private static final String QUEUE_NAME = "test.client.ack";

    private static class Producer {

        private static final String EXCHANGE_NAME = "test.direct.ack";
        private static final ConcurrentNavigableMap<Long, String> CONFIRM_MAP = new ConcurrentSkipListMap<>();

        static void send() throws Exception {
            try (Channel channel = RabbitMQUtils.getChannel()) {

                if (Objects.isNull(channel)) {
                    System.out.println("Channel is null.");
                    System.exit(-1);
                }

                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                String message = "Hello World";

                // confirmSelect方法将当前信道设置成了confirm模式
                channel.confirmSelect();
                // 异步消息确认
                channel.addConfirmListener(new ConfirmListener() {

                    @Override
                    public void handleNack(long deliveryTag, boolean multiple) {
                        System.out.printf("nack: %s %s\n", deliveryTag, multiple);

                        String body = CONFIRM_MAP.get(deliveryTag);
                        System.err.format("Message with body %s has been nack-ed. Sequence number: %d, multiple: %b%n", body, deliveryTag, multiple);

                        if (multiple) {
                            ConcurrentNavigableMap<Long, String> confirmed = CONFIRM_MAP.headMap(deliveryTag, true);
                            confirmed.clear();
                        } else {
                            CONFIRM_MAP.remove(deliveryTag);
                        }
                    }

                    // 确认消息发生到消息服务器broker
                    @Override
                    public void handleAck(long deliveryTag, boolean multiple) {
                        System.out.printf("ack: %s %s\n", deliveryTag, multiple);

                        if (multiple) {
                            ConcurrentNavigableMap<Long, String> confirmed = CONFIRM_MAP.headMap(deliveryTag, true);
                            confirmed.clear();
                        } else {
                            CONFIRM_MAP.remove(deliveryTag);
                        }
                    }
                });

                CONFIRM_MAP.put(channel.getNextPublishSeqNo(), message);
                channel.basicPublish(RabbitMQUtils.getDefaultExchangeName(), QUEUE_NAME, null, message.getBytes());
                CONFIRM_MAP.put(channel.getNextPublishSeqNo(), message);
                channel.basicPublish(EXCHANGE_NAME, QUEUE_NAME, null, message.getBytes());
                System.out.println(CONFIRM_MAP);

                // 同步消息确认 方法阻塞
                // waitForConfirms方法等待broker服务端返回ack或者nack消息，这种模式每发送一条消息就会等待broker代理服务器返回消息
//                if (channel.waitForConfirms()) {
//                    System.out.println("消息发送确认完成 相似方法：waitForConfirmsOrDie()");
//                }

                System.out.println("发送方法结束 end");
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

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println("Waiting for messages ...");

            channel.basicQos(1); // 限制有未确认的消息不能再消费
            channel.basicConsume(QUEUE_NAME, false, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Received " + message);
//                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true); // 消息确认
            }, consumerTag -> {
            });
        }
    }

}
