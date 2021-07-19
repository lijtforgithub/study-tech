package com.ljt.study.zk;

import com.ljt.study.redis.lock.service.LockService;
import com.ljt.study.zk.curator.CuratorLock;
import com.ljt.study.zk.lock.ZkLock;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author LiJingTang
 * @date 2019-11-28 15:28
 */
class LockTest {

    @Test
    void zkLock() {
        test(ZkLock::new, "sub");
    }

    @Test
    void curator() {
        test(CuratorLock::new, "/curator/lock");
    }

    @SneakyThrows
    private void test(LockService lockService, String name) {
        ExecutorService executor = Executors.newCachedThreadPool();

        final int count = 20;
        int[] sum = {0};

        for (int i = 0; i < count; i++) {
            final Lock lock = lockService.getLock(name);
            executor.submit(() -> {
                lock.lock();
                try {
                    sum[0] += 1;
                } finally {
                    lock.unlock();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println(sum[0]);
    }

}
