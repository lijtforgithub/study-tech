package com.ljt.study.tools.jetcache.client;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.MultiLevelCacheBuilder;
import com.alicp.jetcache.embedded.LinkedHashMapCacheBuilder;
import com.alicp.jetcache.redis.RedisCacheBuilder;
import com.alicp.jetcache.support.FastjsonKeyConvertor;
import com.alicp.jetcache.support.JavaValueDecoder;
import com.alicp.jetcache.support.JavaValueEncoder;
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
        GenericObjectPoolConfig pc = new GenericObjectPoolConfig();
        pc.setMinIdle(2);
        pc.setMaxIdle(10);
        pc.setMaxTotal(10);
        JedisPool pool = new JedisPool(pc, "localhost", 6379);
        return RedisCacheBuilder.createRedisCacheBuilder()
                .keyConvertor(FastjsonKeyConvertor.INSTANCE)
                .valueEncoder(JavaValueEncoder.INSTANCE)
                .valueDecoder(JavaValueDecoder.INSTANCE)
                .jedisPool(pool)
                .keyPrefix("redisCache")
                .expireAfterWrite(200, TimeUnit.SECONDS)
                .buildCache();
    }

    @Test
    void multiLevelCache() {
        Cache multiLevelCache = MultiLevelCacheBuilder.createMultiLevelCacheBuilder()
                .addCache(linkedHashMapCache(), redisCache())
                .expireAfterWrite(100, TimeUnit.SECONDS)
                .buildCache();
    }

}
