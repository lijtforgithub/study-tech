package com.ljt.study.rabbitmq.delay;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * @author LiJingTang
 * @date 2020-01-04 14:10
 */
@Slf4j
@Component
public class ConsumerListener implements MessageListener {

    static CountDownLatch cdLatch;

    @Override
    public void onMessage(Message message) {
        String header = JSON.toJSONString(message.getMessageProperties());
        log.debug("接收消息头信息：{}", header);
        log.info("接收消息内容：{}", new String(message.getBody()));

        if (Objects.nonNull(cdLatch)) {
            cdLatch.countDown();
        }
    }

}
