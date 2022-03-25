package com.ljt.study.rocketmq.core;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author jtli3
 * @date 2022-03-25 14:57
 */
@Setter
@Getter
@Accessors(chain = true)
public class MessageContext implements Serializable {

    private static final long serialVersionUID = 7448876794817371729L;

    private String consumerGroup;

}
