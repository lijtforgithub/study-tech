package com.ljt.study.redis.lock;

import com.ljt.study.redis.lock.service.LockService;
import com.ljt.study.redis.lock.service.OrderService;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LiJingTang
 * @date 2021-07-09 13:59
 */
@Configuration
class RedissonLockConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379").setDatabase(0);
        return Redisson.create(config);
    }

    @Bean
    public LockService lockService(RedissonClient redissonClient) {
        return redissonClient::getLock;
    }

    @Bean
    public OrderService orderService() {
        return new OrderService();
    }

}
