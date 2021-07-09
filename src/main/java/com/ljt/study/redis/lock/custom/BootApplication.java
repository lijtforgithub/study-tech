package com.ljt.study.redis.lock.custom;

import com.ljt.study.redis.lock.service.LockService;
import com.ljt.study.redis.lock.service.OrderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author LiJingTang
 * @date 2021-07-09 11:22
 */
@SpringBootApplication
class BootApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }


    @Bean
    public LockService lockService(StringRedisTemplate stringRedisTemplate) {
        return name -> new RedisLock(stringRedisTemplate, name);
    }

    @Bean
    public OrderService orderService() {
        return new OrderService();
    }

}
