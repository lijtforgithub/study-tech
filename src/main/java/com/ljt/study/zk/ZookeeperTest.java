package com.ljt.study.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.ljt.study.Constant.ZK_SERVER_IP;
import static com.ljt.study.Constant.ZK_TIMEOUT;

/**
 * @author LiJingTang
 * @date 2020-03-08 10:21
 */
public class ZookeeperTest {

    /*
     * zk 是有session概念的，没有连接池的概念
     * watch 观察 回调 发生在读类型 get/exist/...
     *  1. new zk 时，传入的watch是session级别的，和path、node没有关系。
     */

    public static void main(String[] args) throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        ZooKeeper zooKeeper = new ZooKeeper(ZK_SERVER_IP, ZK_TIMEOUT, event -> {
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
                case DataWatchRemoved:
                    break;
                case ChildWatchRemoved:
                    break;
            }

            switch (state) {
                case Unknown:
                    break;
                case Disconnected:
                    break;
                case NoSyncConnected:
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
                case Closed:
                    break;
            }
        });

        countDownLatch.await();
        ZooKeeper.States state = zooKeeper.getState();
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

        String path = "/test";
        zooKeeper.create(path, "oldData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        final Stat stat = new Stat();
        byte[] data = zooKeeper.getData(path, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("getData watch: " + event.toString());
                try {
//                    zooKeeper.getData(path, true, stat); // 注册默认的watch session级别
                    zooKeeper.getData(path, this, stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, stat);
        System.out.println(new String(data));
        System.out.println(stat.getMzxid());

        Stat stat1 = zooKeeper.setData(path, "newData".getBytes(), 0);
        System.out.println(stat1.getMzxid());
        zooKeeper.setData(path, "newDataPlus".getBytes(), stat1.getVersion());

        System.out.println("---------- async start ----------");
        zooKeeper.getData(path, false, (rc, path1, ctx, data1, stat2) -> {
            System.out.println("---------- async call back ----------");
            System.out.println(new String(data));
            System.out.println(ctx.toString());
        }, "xxx");
        System.out.println("---------- async over ----------");

        TimeUnit.SECONDS.sleep(10);
    }

}
