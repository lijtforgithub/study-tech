package com.ljt.study.rabbitmq.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2020-01-16 11:12
 */
public class RabbitMQUtils {

    private static Connection connection;

    private static final String DEFAULT_EXCHANGE_NAME = "";
    private static final String DIRECT_EXCHANGE_NAME = "test.client.direct.exchange";
    private static final String FANOUT_EXCHANGE_NAME = "test.client.fanout.exchange";
    private static final String TOPIC_EXCHANGE_NAME = "test.client.topic.exchange";

    public static String getDefaultExchangeName() {
        return DEFAULT_EXCHANGE_NAME;
    }
    public static String getDirectExchangeName() {
        return DIRECT_EXCHANGE_NAME;
    }
    public static String getFanoutExchangeName() {
        return FANOUT_EXCHANGE_NAME;
    }
    public static String getTopicExchangeName() {
        return TOPIC_EXCHANGE_NAME;
    }

    static {
        ConnectionFactory factory = new ConnectionFactory();

        try {
            connection = factory.newConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Channel getChannel() {
        try {
            return connection.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static void closeConnection() {
        if (Objects.nonNull(connection)) {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
