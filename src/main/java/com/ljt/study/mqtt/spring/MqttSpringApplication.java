package com.ljt.study.mqtt.spring;

import com.ljt.study.mqtt.MqttVersion;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author LiJingTang
 * @date 2022-06-29 14:28
 */
@EnableScheduling
@SpringBootApplication
class MqttSpringApplication {

    @Bean
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory connectionFactory) {
        return new RedisLockRegistry(connectionFactory, "mqtt");
    }

    public static void main(String[] args) {
        args = new String[] {"--spring.profiles.active=" + MqttVersion.V3};
        SpringApplication.run(MqttSpringApplication.class, args);
    }

}
