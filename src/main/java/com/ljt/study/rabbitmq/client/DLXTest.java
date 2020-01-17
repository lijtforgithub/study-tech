package com.ljt.study.rabbitmq.client;

import com.google.common.collect.Maps;
import com.ljt.study.rabbitmq.delay.DelayConfig;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2020-01-17 20:34
 */
public class DLXTest {

    public static void main(String[] args) throws Exception {
        Producer.send();

        TimeUnit.SECONDS.sleep(30);
        RabbitMQUtils.closeConnection();
    }


    private static class Producer {

        private static final String EXCHANGE_NORMAL = "test.direct.dlx.normal";
        private static final String EXCHANGE = "test.direct.dlx";
        private static final String QUEUE_NORMAL = "test.client.dlx.normal";
        private static final String QUEUE = "test.client.dlx";
        private static final String DLK = "dlx-key";

        static void send() throws Exception {
            try (Channel channel = RabbitMQUtils.getChannel()) {
                if (Objects.isNull(channel)) {
                    System.out.println("Channel is null.");
                    System.exit(-1);
                }

                channel.exchangeDeclare(EXCHANGE_NORMAL, BuiltinExchangeType.DIRECT, false);
                channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.DIRECT, false);

                Map<String, Object> args = Maps.newHashMapWithExpectedSize(3);
                args.put(DelayConfig.KEY_TTL, 10000);
                args.put(DelayConfig.KEY_DLX, EXCHANGE);
                args.put(DelayConfig.KEY_DLK, DLK);
                channel.queueDeclare(QUEUE_NORMAL, false, true, true, args);
                channel.queueBind(QUEUE_NORMAL, EXCHANGE_NORMAL, QUEUE_NORMAL);

                channel.queueDeclare(QUEUE, false, true, true, null);
                channel.queueBind(QUEUE, EXCHANGE, DLK);

                String message = "延迟消费 死信转发";
                channel.basicPublish(EXCHANGE_NORMAL, QUEUE_NORMAL, null, message.getBytes());
            }
        }
    }

}
