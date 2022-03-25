package com.ljt.study.rocketmq.spring;

import com.ljt.study.YamlPropertySourceFactory;
import com.ljt.study.rocketmq.core.RocketMQConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * @author LiJingTang
 * @date 2021-07-07 09:19
 */
@Import(RocketMQConfig.class)
@SpringBootApplication
@PropertySource(value = "classpath:rocketmq/application.yml", factory = YamlPropertySourceFactory.class)
class RocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(RocketApplication.class, args);
    }

}
