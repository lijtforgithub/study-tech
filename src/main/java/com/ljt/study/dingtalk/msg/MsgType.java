package com.ljt.study.dingtalk.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LiJingTang
 * @date 2022-07-25 9:29
 */
@Getter
@AllArgsConstructor
public enum MsgType {

    TEXT("text"),
    MARKDOWN("markdown");

    private final String type;

}
