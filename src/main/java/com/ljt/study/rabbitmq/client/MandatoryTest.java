package com.ljt.study.rabbitmq.client;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2020-01-17 13:53
 */
public class MandatoryTest {

    public static void main(String[] args) throws Exception {
        Producer.send();

        TimeUnit.SECONDS.sleep(10);
        RabbitMQUtils.closeConnection();
    }


    private static final String EXCHANGE = "test.direct.mandatory";

    private static class Producer {

        static void send() throws Exception {
            // channel 不能提前关闭 不然没效果
            Channel channel = RabbitMQUtils.getChannel();
            if (Objects.isNull(channel)) {
                System.out.println("Channel is null.");
                System.exit(-1);
            }

            channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.DIRECT, false);

            String message = "Hello World!";
            channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) ->
                    System.out.println(new String(body, StandardCharsets.UTF_8) + "未投递成功 routingKey=" + routingKey));

            channel.basicPublish(EXCHANGE, "unbind-key", true, MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes());
        }
    }

}
