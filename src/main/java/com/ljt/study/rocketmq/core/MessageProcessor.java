package com.ljt.study.rocketmq.core;

import org.apache.rocketmq.common.message.MessageExt;

/**
 * @author jtli3
 * @date 2022-03-31 16:30
 */
public interface MessageProcessor {

    boolean beforeHandle(MessageExt message, MessageContext context);

    void afterHandle(MessageExt message, MessageContext context);

}
