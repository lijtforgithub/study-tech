package com.ljt.study.tools.jetcache.client;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.MultiLevelCacheBuilder;
import com.alicp.jetcache.embedded.CaffeineCacheBuilder;
import com.alicp.jetcache.embedded.LinkedHashMapCacheBuilder;
import com.alicp.jetcache.redis.RedisCacheBuilder;
import com.alicp.jetcache.redis.lettuce.RedisLettuceCacheBuilder;
import com.alicp.jetcache.support.FastjsonKeyConvertor;
import com.alicp.jetcache.support.JavaValueDecoder;
import com.alicp.jetcache.support.JavaValueEncoder;
import io.lettuce.core.RedisClient;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2021-12-14 13:50
 */
class BuilderTest {

    @Test
    Cache<String, Integer> linkedHashMapCache() {
        return LinkedHashMapCacheBuilder.createLinkedHashMapCacheBuilder()
                .limit(100)
                .expireAfterWrite(200, TimeUnit.SECONDS)
                .buildCache();
    }

    @Test
    Cache<String, Integer> redisCache() {
        GenericObjectPoolConfig<?> pc = new GenericObjectPoolConfig<>();
        pc.setMinIdle(2);
        pc.setMaxIdle(10);
        pc.setMaxTotal(10);
        JedisPool pool = new JedisPool(pc, "localhost", 6379);
        return RedisCacheBuilder.createRedisCacheBuilder()
                .keyConvertor(FastjsonKeyConvertor.INSTANCE)
                .valueEncoder(JavaValueEncoder.INSTANCE)
                .valueDecoder(JavaValueDecoder.INSTANCE)
                .jedisPool(pool)
                .keyPrefix("redis:cache")
                .expireAfterWrite(200, TimeUnit.SECONDS)
                .buildCache();
    }

    @SneakyThrows
    @Test
    void multiLevelCache() {
        Cache<String, String> caffeineCache = CaffeineCacheBuilder.createCaffeineCacheBuilder()
                .cachePenetrateProtect(true)
                .keyConvertor(FastjsonKeyConvertor.INSTANCE)
                .buildCache();

        Cache<String, String> lettuceCache = RedisLettuceCacheBuilder.createRedisLettuceCacheBuilder()
                .keyConvertor(FastjsonKeyConvertor.INSTANCE)
                .valueEncoder(JavaValueEncoder.INSTANCE)
                .valueDecoder(JavaValueDecoder.INSTANCE)
                .redisClient(RedisClient.create("redis://127.0.0.1"))
                .keyPrefix("token:")
                .buildCache();

        Cache<String, String> multiLevelCache = MultiLevelCacheBuilder.createMultiLevelCacheBuilder()
                .addCache(caffeineCache, lettuceCache)
                .loader(key -> RandomStringUtils.randomAlphanumeric(10))
                .expireAfterWrite(15000, TimeUnit.MILLISECONDS)
                .addMonitor(event -> System.out.println("redis: " + event.getClass().getName()))
                .buildCache();

        System.out.println(multiLevelCache.get("xxoo"));

        TimeUnit.SECONDS.sleep(30);
    }

}
