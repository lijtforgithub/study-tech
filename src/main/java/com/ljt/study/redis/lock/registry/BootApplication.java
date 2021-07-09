package com.ljt.study.redis.lock.registry;

import com.ljt.study.redis.lock.service.LockService;
import com.ljt.study.redis.lock.service.OrderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

/**
 * @author LiJingTang
 * @date 2021-07-09 14:24
 */
@SpringBootApplication
class BootApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }


    @Bean
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory connectionFactory) {
        return new RedisLockRegistry(connectionFactory, "test");
    }

    @Bean
    public LockService lockService(RedisLockRegistry redisLockRegistry) {
        return redisLockRegistry::obtain;
    }

    @Bean
    public OrderService orderService() {
        return new OrderService();
    }

}
