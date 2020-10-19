package com.ljt.study.zk;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author LiJingTang
 * @date 2019-11-28 15:28
 */
public class LockTest {

    private static final Logger logger = LoggerFactory.getLogger(LockTest.class);

    private static final int THREAD_NUM = 10;
    private static final CountDownLatch latch = new CountDownLatch(THREAD_NUM);

    public static void main(String[] args) {
        for (int i = 0; i < THREAD_NUM; i++) {
            final int index = i + 1;

            new Thread(() -> {
                ZooKeeper zk = ZKFactory.newConnection();
                DistributedLock disLock = new DistributedLock("【线程-" + index + "】", zk);

                disLock.setToDoService(() -> {
                    logger.info("业务逻辑：修改第行{}代码", index);
                    latch.countDown();
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                try {
                    if (disLock.lock()) {
                        disLock.todo();
                        disLock.unLock();
                    }
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        try {
            latch.await();
            logger.info("所有线程运行结束！GAME OVER");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private interface ToDoService {

        void dodo();

    }


    private static class DistributedLock implements Watcher {

        private static final Logger logger = LoggerFactory.getLogger(DistributedLock.class);

        private static final String LOCK_PATH = "/disLocks";
        private static final String LOCK_SUB_PATH = LOCK_PATH + "/sub";

        private String client;
        private ZooKeeper zk;

        private String selfPath;
        private String waitPath;

        private ToDoService toDoService;

        public DistributedLock(String client, ZooKeeper zk) {
            super();
            this.client = client;
            this.zk = zk;
        }

        public void setToDoService(ToDoService toDoService) {
            this.toDoService = toDoService;
        }

        private void createPath(String data) throws KeeperException, InterruptedException {
            if (this.zk.exists(LOCK_PATH, this) == null) {
                data = StringUtils.trimToEmpty(data);
                logger.info("创建节点 => {}", this.zk.create(LOCK_PATH, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));
            }
        }

        public boolean lock() throws KeeperException, InterruptedException {
            this.createPath("该节点由【" + Thread.currentThread().getName() + "】创建");
            this.selfPath = this.zk.create(LOCK_SUB_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.info("{}创建锁路径{} ", this.client, this.selfPath);

            return this.checkMinPath();
        }

        public void unLock() {
            try {
                if (this.zk.exists(this.selfPath, false) == null) {
                    logger.error("{}本节点已经不存在了...", this.selfPath);
                    return;
                }

                this.zk.delete(this.selfPath, -1);
                logger.info("{}删除本节点{}", this.client, this.selfPath);
                this.zk.close();
                logger.info("{} 关闭链接", this.client);
            } catch (InterruptedException | KeeperException e) {
                e.printStackTrace();
            }
        }

        public void todo() throws KeeperException, InterruptedException {
            if (this.zk.exists(this.selfPath, false) == null) {
                logger.error("{}本节点已不在了...{}", this.client, this.selfPath);
                return;
            }

            logger.info("{}获取锁成功，赶紧干活！", this.client);

            if (this.toDoService != null) {
                this.toDoService.dodo();
            }
        }

        private boolean checkMinPath() throws KeeperException, InterruptedException {
            List<String> subNodes = this.zk.getChildren(LOCK_PATH, false);
            Collections.sort(subNodes);

            int index = subNodes.indexOf(this.selfPath.substring(LOCK_PATH.length() + 1));

            switch (index) {
                case -1: {
                    logger.error("{}本节点已不在了...{}", this.client, this.selfPath);
                    return false;
                }
                case 0: {
                    logger.info("{}子节点中，我果然是老大{}", this.client, this.selfPath);
                    return true;
                }
                default: {
                    this.waitPath = LOCK_PATH + "/" + subNodes.get(index - 1);
                    logger.info("{}获取子节点中，排在我前面的{}", this.client, this.waitPath);

                    try {
                        this.zk.getData(this.waitPath, this, new Stat()); // 为了注册Watcher
                        return false;
                    } catch (KeeperException e) {
                        if (this.zk.exists(this.waitPath, false) == null) {
                            logger.info("{}子节点中，排在我前面的{}已失踪，幸福来得太突然？", this.client, this.waitPath);
                            return checkMinPath();
                        } else {
                            throw e;
                        }
                    }
                }
            }
        }

        @Override
        public void process(WatchedEvent event) {
            if (Event.EventType.NodeDeleted == event.getType() && event.getPath().equals(this.waitPath)) {
                logger.info("{}收到情报：排我前面的家伙已挂，我是不是可以出山了？", this.client);

                try {
                    if (this.checkMinPath()) {
                        this.todo();
                        this.unLock();
                    }
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
