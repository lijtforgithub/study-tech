package com.ljt.study.rocketmq.client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;

import static com.ljt.study.rocketmq.client.RocketMQUtils.*;

/**
 * @author LiJingTang
 * @date 2021-06-29 10:18
 */
@Slf4j
public class TransactionProducerTest {

    @SneakyThrows
    public static void main(String[] args) {
        TransactionMQProducer producer = new TransactionMQProducer(CLIENT_GROUP);
        producer.setNamesrvAddr(NAME_SERVER);
        producer.start();

        producer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
                log.info("executeLocalTransaction：{} {} {}", msg.getTransactionId(), new String(msg.getBody()), arg);
                return LocalTransactionState.UNKNOW;
            }

            /**
             * broker 回调方法
             */
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                log.info("checkLocalTransaction：{} {}", msg.getTransactionId(), new String(msg.getBody()));
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        });

        TransactionSendResult result = producer.sendMessageInTransaction(new Message(CLIENT_TOPIC,
                        "事务消息1".getBytes(StandardCharsets.UTF_8)), "小参");
        log.info("事务消息发送结果：{}", result.toString());
        result = producer.sendMessageInTransaction(new Message(CLIENT_TOPIC,
                        "事务消息2".getBytes(StandardCharsets.UTF_8)), null);
        log.info("事务消息发送结果：{}", result.toString());
    }

}
