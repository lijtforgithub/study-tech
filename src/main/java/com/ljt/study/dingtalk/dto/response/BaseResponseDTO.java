package com.ljt.study.dingtalk.dto.response;

import lombok.Data;

import java.util.Objects;

/**
 * DingTalk响应消息基本类
 *
 * @author LiJingTang
 * @date 2022-07-22 11:06
 */
@Data
public class BaseResponseDTO {

    /**
     * 错误编码 0=成功 否则失败
     */
    private Integer errcode;

    /**
     * 错误描述 ok=成功 否则失败
     */
    private String errmsg;

    public boolean isSuccess() {
        return Objects.nonNull(errcode) && errcode == 0;
    }

}
