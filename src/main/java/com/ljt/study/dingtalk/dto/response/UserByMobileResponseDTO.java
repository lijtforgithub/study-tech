package com.ljt.study.dingtalk.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户响应消息
 *
 * @author LiJingTang
 * @date 2022-07-22 11:21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserByMobileResponseDTO extends BaseResponseDTO {

    private User result;


    @Data
    public static class User {

        /**
         * 用户ID
         */
        private String userid;

    }

}

