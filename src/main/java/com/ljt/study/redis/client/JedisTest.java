package com.ljt.study.redis.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import static com.ljt.study.Constant.*;

/**
 * @author LiJingTang
 * @date 2020-01-04 14:22
 */
@Slf4j
public class JedisTest {

    private static final String KEY_PREFIX = REDIS_TEST_KEY_PREFIX + "client:";

    /**
     * 一秒写入次数
     */
    @Test
    public void testCount() {
        Jedis jedis = new Jedis(LOCAL_HOST, REDIS_DEF_PORT, 10000);

        long startTime = System.currentTimeMillis();
        long endTime = 0L;
        int i = 0;

        String key = KEY_PREFIX + "i-";

        try {
            while (endTime - startTime <= 1000) {
                endTime = System.currentTimeMillis();
                i++;
                jedis.set(key + i, i + "");
            }
        } finally {
            jedis.close();
        }

        log.info("每秒操作【{}】次", i);
    }

    @Test
    public void testPool() {
        JedisPoolConfig conf = new JedisPoolConfig();
        // 最大连接数
        conf.setMaxTotal(100);
        // 最大空闲数
        conf.setMaxIdle(50);
        // 最大等待毫秒数
        conf.setMaxWaitMillis(20000);
        // 创建连接池
        @SuppressWarnings("resource")
        JedisPool pool = new JedisPool(conf, LOCAL_HOST);
        // 从连接池中获取单个连接
        Jedis jedis = pool.getResource();
        // 如果需要密码
//        jedis.auth(password);

        jedis.set(KEY_PREFIX + "pool", JedisPoolConfig.class.getName());

        log.info("DB = {}", jedis.getDB());
    }

}
