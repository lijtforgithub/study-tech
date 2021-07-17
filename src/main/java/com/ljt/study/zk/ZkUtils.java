package com.ljt.study.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static com.ljt.study.Constant.ZK_SERVER;
import static com.ljt.study.Constant.ZK_TIMEOUT;

/**
 * @author LiJingTang
 * @date 2019-11-28 15:12
 */
@Slf4j
public class ZkUtils {

    public static final String RMI_PATH = "/rmi";
    public static final String RMI_SERVICE_PATH = RMI_PATH + "/service";

    private ZkUtils() {
        super();
    }

    public static ZooKeeper newInstance() {
        ZooKeeper zookeeper = null;
        final CountDownLatch latch = new CountDownLatch(1);

        try {
            zookeeper = new ZooKeeper(ZK_SERVER, ZK_TIMEOUT, event -> {
                if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                    latch.countDown();
                }
            });

            latch.await();
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
        }

        return zookeeper;
    }

    public static void createRootPath(ZooKeeper zk, String path) throws KeeperException, InterruptedException {
        if (zk.exists(path, false) == null) {
            zk.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

}
