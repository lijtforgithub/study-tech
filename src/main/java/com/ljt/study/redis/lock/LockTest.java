package com.ljt.study.redis.lock;

import com.ljt.study.AbstractTest;
import com.ljt.study.redis.lock.service.OrderService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author LiJingTang
 * @date 2021-07-09 14:03
 */
public class LockTest extends AbstractTest {

    @Test
    void redisson() {
        setApplicationContext(RedissonLockConfig.class);
        final OrderService orderService = applicationContext.getBean(OrderService.class);
        test(orderService);
    }

    @Test
    void redLock() {
        setApplicationContext(RedissonRedLockConfig.class);
        final OrderService orderService = applicationContext.getBean(OrderService.class);
        test(orderService);
    }

    @SneakyThrows
    protected void test(final OrderService orderService) {
        ExecutorService executor = Executors.newCachedThreadPool();
        ((ThreadPoolExecutor) executor).prestartAllCoreThreads();

        final int count = 9;
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            int fi = i;
            executor.submit(() -> {
                orderService.grabOrder((fi % OrderService.COUNT) + 1);
                latch.countDown();
            });
        }
        latch.await();
    }

}
