package com.ljt.study.rocketmq.core;

import org.apache.rocketmq.common.message.MessageExt;

/**
 * @author jtli3
 * @date 2022-03-25 10:34
 */
public interface MessagePostProcessor {

    void postProcessBeforeHandle(MessageExt message, MessageContext context);

    void postProcessAfterHandle(MessageExt message, MessageContext context);

}
