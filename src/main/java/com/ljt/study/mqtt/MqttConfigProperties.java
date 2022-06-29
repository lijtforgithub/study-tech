package com.ljt.study.mqtt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author LiJingTang
 * @date 2022-06-29 13:51
 */
@Data
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfigProperties {

    private String serverUri;
    private String userName;
    private String password;
    private String topic;

}
