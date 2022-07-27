package com.ljt.study.dingtalk.dto.request;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author LiJingTang
 * @date 2022-07-22 10:10
 */
@Data
@Accessors(chain = true)
public class AccessTokenRequestDTO {

    /**
     * 已创建的企业内部应用的AppKey
     */
    private String appKey;
    /**
     * 已创建的企业内部应用的AppSecret
     */
    private String appSecret;

}
