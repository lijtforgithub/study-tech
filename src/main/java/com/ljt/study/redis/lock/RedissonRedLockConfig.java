package com.ljt.study.redis.lock;

import com.ljt.study.redis.lock.service.LockService;
import com.ljt.study.redis.lock.service.OrderService;
import org.redisson.Redisson;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LiJingTang
 * @date 2021-07-09 14:14
 */
@Configuration
public class RedissonRedLockConfig {

    @Bean
    public RedissonClient redissonClient1() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379").setDatabase(0);
        return Redisson.create(config);
    }

    @Bean
    public RedissonClient redissonClient2() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://150.158.110.118:6379").setPassword("7NsLHkl3D3svzvJA").setDatabase(0);
        return Redisson.create(config);
    }

    @Bean
    public RedissonClient redissonClient3() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://150.158.110.118:7379").setPassword("7NsLHkl3D3svzvJA").setDatabase(0);
        return Redisson.create(config);
    }

    @Bean
    public LockService lockService(RedissonClient redissonClient1, RedissonClient redissonClient2, RedissonClient redissonClient3) {
        return name -> {
            RLock rLock1 = redissonClient1.getLock(name);
            RLock rLock2 = redissonClient2.getLock(name);
            RLock rLock3 = redissonClient3.getLock(name);
            return new RedissonRedLock(rLock1, rLock2, rLock3);
        };
    }

    @Bean
    public OrderService orderService() {
        return new OrderService();
    }

}
