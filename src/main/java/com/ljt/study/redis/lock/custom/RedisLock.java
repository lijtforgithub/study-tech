package com.ljt.study.redis.lock.custom;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author LiJingTang
 * @date 2021-07-08 15:30
 */
@Slf4j
class RedisLock implements Lock {

    private static final long DEF_TIME = 10L;

    private final StringRedisTemplate stringRedisTemplate;
    private final String name;
    private final String uuid = UUID.randomUUID().toString();

    public RedisLock(StringRedisTemplate stringRedisTemplate, String name) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.name = StringUtils.defaultIfBlank(name, uuid);
    }

    @Override
    public void lock() {
        while (!tryLock()) {
            sleep(100, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void lockInterruptibly() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock() {
        /*
         * 超时时间：如果锁没执行到释放，比如业务逻辑执行一半，运维重启服务，或服务器挂了，没走 finally
         * 原子操作：写key和超时时间应该一次加，不应该分2行代码，
         */
        Assert.notNull(stringRedisTemplate, "stringRedisTemplate为空");
        // 为了实现锁的可重入 还要判断当前线程是否已经获得锁 这里未做实现
        final boolean status = Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(name, uuid, DEF_TIME, TimeUnit.SECONDS));
        if (status) {
            log.info("{} 获取到锁 {}", Thread.currentThread().getName(), name);
            renew();
        }
        return status;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        Assert.notNull(unit, "时间单位为空");

        final long deadline = System.nanoTime() + unit.toNanos(time);
        do {
            final boolean status = tryLock();
            if (status) {
                return true;
            }
        } while (System.nanoTime() <= deadline);

        return false;
    }

    @Override
    public void unlock() {
        // 避免释放别人的锁
        if (uuid.equals(stringRedisTemplate.opsForValue().get(name))) {
            stringRedisTemplate.delete(name);
        }
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

    /**
     * 为锁续约 防止业务代码执行时间太长
     */
    private void renew() {
        Executors.newSingleThreadExecutor().submit(() -> {
            String v = stringRedisTemplate.opsForValue().get(name);
            while (uuid.equals(v)) {
                sleep(DEF_TIME / 3, TimeUnit.SECONDS);
                stringRedisTemplate.expire(name, DEF_TIME, TimeUnit.SECONDS);
            }
        });
    }

    private void sleep(long time, TimeUnit unit) {
        try {
            unit.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
