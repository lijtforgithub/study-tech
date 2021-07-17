package com.ljt.study.zk.lock;

import com.ljt.study.zk.ZkUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author LiJingTang
 * @date 2021-07-17 18:53
 */
public class ZkLock implements Lock {

    private final String name;
    private final DistributedLock distributedLock;

    public ZkLock(String name) {
        this.name = name;
        this.distributedLock = new DistributedLock(ZkUtils.newInstance(), this.name);
    }

    @Override
    public void lock() {
        distributedLock.lock();
    }

    @Override
    public void lockInterruptibly() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock() {
        return distributedLock.tryLock();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unlock() {
        distributedLock.unLock();
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }


    @Slf4j
    private static class DistributedLock implements Watcher {

        private static final String LOCK_PATH = "/lock";
        private CountDownLatch latch;

        private final ZooKeeper zk;
        private final String name;

        private String selfPath;
        private String waitPath;

        public DistributedLock(ZooKeeper zk, String name) {
            this.zk = zk;
            this.name = String.format("%s/%s", LOCK_PATH, name);
        }

        public void lock() {
            final boolean status = tryLock();

            if (!status) {
                latch = new CountDownLatch(1);

                try {
                    latch.await();
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
        }

        public boolean tryLock() {
            try {
                ZkUtils.createRootPath(zk, LOCK_PATH);
                selfPath = zk.create(name, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                log.info("{} 创建锁路径 {} ", Thread.currentThread().getName(), selfPath);
                return isMinPath();
            } catch (InterruptedException | KeeperException e) {
                e.printStackTrace();
                log.error(e.getMessage());
                return false;
            }
        }

        public void unLock() {
            try {
                if (zk.exists(selfPath, false) == null) {
                    return;
                }

                zk.delete(selfPath, -1);
            } catch (InterruptedException | KeeperException e) {
                log.error(e.getMessage());
            }
        }

        private boolean isMinPath() throws KeeperException, InterruptedException {
            List<String> subNodes = zk.getChildren(LOCK_PATH, false);
            Collections.sort(subNodes);

            int index = subNodes.indexOf(selfPath.substring(LOCK_PATH.length() + 1));

            switch (index) {
                case -1: {
                    return false;
                }
                case 0: {
                    if (latch != null) {
                        latch.countDown();
                    }
                    return true;
                }
                default: {
                    waitPath = String.format("%s/%s", LOCK_PATH, subNodes.get(index - 1));

                    try {
                        zk.getData(waitPath, this, new Stat()); // 为了注册Watcher
                        return false;
                    } catch (KeeperException e) {
                        return isMinPath();
                    }
                }
            }
        }

        @Override
        public void process(WatchedEvent event) {
            log.info("watcher: {}", event.toString());

            if (Event.EventType.NodeDeleted == event.getType() && event.getPath().equals(waitPath)) {
                try {
                    isMinPath();
                } catch (KeeperException | InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

}
