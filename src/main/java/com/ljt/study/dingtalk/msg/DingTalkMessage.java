package com.ljt.study.dingtalk.msg;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author LiJingTang
 * @date 2022-07-25 9:30
 */
@Getter
@NoArgsConstructor
public abstract class DingTalkMessage {

    private String msgtype;

    protected DingTalkMessage(String msgtype) {
        this.msgtype = msgtype;
    }

}
