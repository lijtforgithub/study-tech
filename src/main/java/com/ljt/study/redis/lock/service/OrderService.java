package com.ljt.study.redis.lock.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author LiJingTang
 * @date 2021-07-09 09:13
 */
@Slf4j
@Component
public class OrderService {

    @Autowired
    private LockService lockService;

    public static final int COUNT = 3;

    private static final ConcurrentHashMap<Integer, Boolean> CACHE = new ConcurrentHashMap<>();

    static {
        for (int i = 1; i <= COUNT; i++) {
            CACHE.put(i, Boolean.FALSE);
        }
    }

    @SneakyThrows
    private boolean grab(Integer orderId) {
        final Boolean flag = CACHE.get(orderId);
        Assert.notNull(flag, "订单不存在：" + orderId);
        TimeUnit.SECONDS.sleep(new Random().nextInt(5));
        if (Boolean.FALSE.equals(flag)) {
            CACHE.put(orderId, Boolean.TRUE);
            return true;
        }
        return false;
    }

    /**
     * 模拟抢单
     *
     * @param orderId 订单ID
     */
    @SneakyThrows
    public void grabOrder(Integer orderId) {
        Assert.notNull(orderId, "订单ID为空");
        final Lock lock = lockService.getLock(getLockName(orderId));
        lock.lock();

        try {
            final boolean flag = grab(orderId);
            log.info("{} 抢单-{} {}", Thread.currentThread().getName(), orderId, flag ? "成功" : "失败");
        } finally {
            lock.unlock();
        }
    }

    private String getLockName(Integer orderId) {
        return "dlc:order:grab:" + orderId;
    }

}
