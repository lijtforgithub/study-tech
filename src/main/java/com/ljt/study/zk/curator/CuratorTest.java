package com.ljt.study.zk.curator;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.ljt.study.Constant.ZK_SERVER;

/**
 * @author LiJingTang
 * @date 2021-07-17 22:21
 */
@Slf4j
class CuratorTest {

    static CuratorFramework client() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(ZK_SERVER, retryPolicy);
        client.start();

        try {
            final String path = "/curator";
            Stat stat = client.checkExists().forPath(path);
            if (stat == null) {
                client.create().forPath(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return client;
    }

    @Test
    @SneakyThrows
    void createPath() {
        final CuratorFramework client = client();
        final String path = "/curator/test";

        Stat stat = client.checkExists().forPath(path);
        if (stat == null) {
            client.create().forPath(path, "test-data".getBytes());
        }
        String data = new String(client.getData().forPath(path));
        log.info(data);

        client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path + "/sub");
//        client.create().withTtl(5000).withMode(CreateMode.PERSISTENT_WITH_TTL).forPath(path + "/ttl");
    }

    @Test
    @SneakyThrows
    void createParent() {
        final CuratorFramework client = client();
        final String path = "/curator/test1/test2";
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
        // creatingParentContainersIfNeeded 用法不明
        client.delete().deletingChildrenIfNeeded().forPath("/curator");
    }

    @Test
    @SneakyThrows
    void transaction() {
        final CuratorFramework client = client();

        // 定义几个基本操作
        CuratorOp createOp = client.transactionOp().create().forPath("/curator/transaction", "some data".getBytes());
        CuratorOp setDataOp = client.transactionOp().setData().forPath("/curator", "other data".getBytes());
        CuratorOp deleteOp = client.transactionOp().delete().forPath("/curator");

        // 事务执行结果
        List<CuratorTransactionResult> results = client.transaction().forOperations(createOp, setDataOp, deleteOp);

        // 遍历输出结果
        for (CuratorTransactionResult result : results) {
            log.info("执行结果是： {} => {}", result.getForPath(), result.getType());
        }
    }

    @Test
    @SneakyThrows
    void leader() {
        final String path = "/curator/leader";
        ExecutorService executor = Executors.newFixedThreadPool(3);

        final int count = 3;
        for (int i = 0; i < count; i++) {
            Runnable task = new LeaderSelectorTask(client(), path);
//            Runnable task = new LeaderLatchTask(client(), path);
            executor.submit(task);
        }

        TimeUnit.SECONDS.sleep(30);
    }

    @Test
    @SneakyThrows
    void cache() {
        final CuratorFramework client = client();
        final String path = "/curator/cache";

        CacheTask task = new CacheTask(client, path).nodeCache();
        log.info("开始监听：start");
        TimeUnit.SECONDS.sleep(5);
        client.create().creatingParentsIfNeeded().forPath(path);

        TimeUnit.SECONDS.sleep(5);
        log.info("节点设置数据");
        client.setData().forPath(path, "节点数据".getBytes());

        TimeUnit.SECONDS.sleep(5);
        log.info("添加子节点");
        String test = client.create().forPath(path + "/test");
        TimeUnit.SECONDS.sleep(5);
        log.info("子节点设置数据");
        client.setData().forPath(test, "子节点数据".getBytes());

        TimeUnit.SECONDS.sleep(5);
        log.info("添加子节点的子节点");
        client.create().forPath(test + "/test1");

        task.printData();
        TimeUnit.SECONDS.sleep(2);
        task.close();
        TimeUnit.SECONDS.sleep(2);
        client.delete().deletingChildrenIfNeeded().forPath(path);
        TimeUnit.SECONDS.sleep(2);
    }

}
