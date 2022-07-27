package com.ljt.study.dingtalk.dto.request;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户响应消息
 *
 * @author LiJingTang
 * @date 2022-07-22 11:21
 */
@Data
@Accessors(chain = true)
public class UserByMobileRequestDTO {

    /**
     * 手机号码
     */
    private String mobile;

}

