package com.ljt.study.tools.jetcache.client;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.embedded.CaffeineCache;
import com.alicp.jetcache.embedded.EmbeddedCacheConfig;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2021-12-13 15:00
 */
class CacheTest {

    private static Cache<Integer, String> cache;

    @BeforeAll
    static void beforeAll() {
        EmbeddedCacheConfig<Integer, String> config = new EmbeddedCacheConfig<>();
        cache = new CaffeineCache<>(config);
    }

    @SneakyThrows
    @Test
    void put() {
        cache.put(1, RandomStringUtils.randomAlphabetic(1), 5, TimeUnit.SECONDS);
        System.out.println(cache.get(1));
        TimeUnit.SECONDS.sleep(5);
        System.out.println(cache.get(1));
    }

}
