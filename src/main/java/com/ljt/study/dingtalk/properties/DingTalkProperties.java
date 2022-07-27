package com.ljt.study.dingtalk.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * DingTalk属性配置类
 *
 * @author LiJingTang
 * @date 2022-07-22 9:15
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "dingtalk")
public class DingTalkProperties {

    /**
     * 开关
     */
    private Boolean open;
    private String baseUrl = "https://oapi.dingtalk.com/";

    private String agentId;
    private String appKey;
    private String appSecret;

}
