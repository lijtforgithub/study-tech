package com.ljt.study.tools.jetcache.client;

import com.alicp.jetcache.AutoReleaseLock;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.embedded.CaffeineCache;
import com.alicp.jetcache.embedded.EmbeddedCacheConfig;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
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

    @Test
    void tryLock() {
        // 使用try-with-resource方式，可以自动释放锁
        try (AutoReleaseLock lock = cache.tryLock(1, 100, TimeUnit.SECONDS)){
            if (lock != null){
                System.out.println("tryLock");
            }
        }
    }

    @SneakyThrows
    @Test
    void tryLockAndRun() {
        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int finalI = i;
            list.add(new Thread(() -> {
                boolean hasRun = cache.tryLockAndRun(1, 100, TimeUnit.SECONDS, () -> {
                    System.out.println("tryLockAndRun " + finalI);
                });
                System.out.println(finalI + " " + hasRun);
            }));
        }
        list.forEach(Thread::start);

        TimeUnit.SECONDS.sleep(3);
    }

}
