package com.ljt.study.zk;

import lombok.SneakyThrows;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.ljt.study.Constant.ZK_SERVER;
import static com.ljt.study.Constant.ZK_TIMEOUT;

/**
 * @author LiJingTang
 * @date 2020-03-08 10:21
 */
class ClientTest {

    /*
     * zk 是有session概念的，没有连接池的概念
     * watch 观察 回调 发生在读类型 get/exist/...
     *  1. new zk 时，传入的watch是session级别的，和path、node没有关系。
     */

    @Test
    @SneakyThrows
    void zk() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final ZooKeeper zk = new ZooKeeper(ZK_SERVER, ZK_TIMEOUT, event -> {
            Watcher.Event.EventType type = event.getType();
            Watcher.Event.KeeperState state = event.getState();
            System.out.println("new zk watch" + event.toString());

            switch (type) {
                case None:
                    break;
                case NodeCreated:
                    break;
                case NodeDeleted:
                    break;
                case NodeDataChanged:
                    break;
                case NodeChildrenChanged:
                    break;
            }

            switch (state) {
                case Disconnected:
                    break;
                case SyncConnected:
                    System.out.println("SyncConnected");
                    countDownLatch.countDown();
                    break;
                case AuthFailed:
                    break;
                case ConnectedReadOnly:
                    break;
                case SaslAuthenticated:
                    break;
                case Expired:
                    break;
            }
        });

        countDownLatch.await();
        ZooKeeper.States state = zk.getState();

        switch (state) {
            case CONNECTING:
                System.out.println("connecting...");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("connected...");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }
    }

    @Test
    @SneakyThrows
    void test() {
        final ZooKeeper zk = ZkUtils.newInstance();

        String path = "/test";
        zk.create(path, "oldData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        final Stat stat = new Stat();
        byte[] data = zk.getData(path, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("getData watch: " + event.toString());
                try {
//                    zk.getData(path, true, stat); // 注册默认的watch session级别
                    zk.getData(path, this, stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, stat);
        System.out.println(new String(data));
        System.out.println(stat.getMzxid());

        Stat stat1 = zk.setData(path, "newData".getBytes(), 0);
        System.out.println(stat1.getMzxid());
        zk.setData(path, "newDataPlus".getBytes(), stat1.getVersion());

        System.out.println("---------- async start ----------");
        zk.getData(path, false, (rc, path1, ctx, data1, stat2) -> {
            System.out.println("---------- async call back ----------");
            System.out.println(new String(data));
            System.out.println(ctx.toString());
        }, "xxx");
        System.out.println("---------- async over ----------");

        zk.sync(path, null, null);

        TimeUnit.SECONDS.sleep(10);
    }

}
