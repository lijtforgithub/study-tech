package com.ljt.study.zk.curator;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static com.ljt.study.Constant.ZK_SERVER;

/**
 * InterProcessMutex：分布式可重入排它锁
 * InterProcessSemaphoreMutex：分布式排它锁
 * InterProcessReadWriteLock：分布式读写锁
 * InterProcessMultiLock：将多个锁作为单个实体管理的容器
 *
 * @author LiJingTang
 * @date 2021-07-17 21:52
 */
@Slf4j
public class CuratorLock implements Lock {

    private final InterProcessLock lock;

    public CuratorLock(String name) {
        CuratorFramework client = CuratorFrameworkFactory.newClient(ZK_SERVER, new RetryNTimes(5, 5000));
        client.start();

        Assert.isTrue(client.getState() == CuratorFrameworkState.STARTED, "客户端启动失败");
        this.lock = new InterProcessMutex(client, name);
    }

    @Override
    @SneakyThrows
    public void lock() {
        lock.acquire();
    }

    @Override
    public void lockInterruptibly() {
        throw new UnsupportedOperationException();
    }

    @Override
    @SneakyThrows
    public boolean tryLock() {
        return lock.acquire(0, TimeUnit.SECONDS);
    }

    @Override
    @SneakyThrows
    public boolean tryLock(long time, TimeUnit unit) {
        return lock.acquire(time, unit);
    }

    @Override
    @SneakyThrows
    public void unlock() {
        lock.release();
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

}
