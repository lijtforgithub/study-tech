package com.ljt.study.dingtalk.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author LiJingTang
 * @date 2022-07-22 10:10
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AccessTokenResponseDTO extends BaseResponseDTO {

    /**
     * 生成的accessToken
     */
    private String access_token;
    /**
     * accessToken的过期时间，单位秒
     */
    private Long expires_in;

}
