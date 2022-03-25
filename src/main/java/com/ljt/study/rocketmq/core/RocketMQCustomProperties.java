package com.ljt.study.rocketmq.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jtli3
 * @date 2022-03-25 16:17
 */
@Data
@ConfigurationProperties(prefix = "rocketmq.custom")
public class RocketMQCustomProperties {

    private String repeatPrefix = "rocketmq:repeat";
    private Long repeatCacheTime = 3600L;

}
