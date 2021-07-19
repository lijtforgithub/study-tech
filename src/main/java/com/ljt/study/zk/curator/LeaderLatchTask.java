package com.ljt.study.zk.curator;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;

import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2021-07-18 16:21
 */
@Slf4j
class LeaderLatchTask implements Runnable {

    private final CuratorFramework client;
    private final String path;

    LeaderLatchTask(CuratorFramework client, String path) {
        this.client = client;
        this.path = path;
    }

    @Override
    public void run() {
        LeaderLatch leaderLatch = new LeaderLatch(client, path, Thread.currentThread().getName());

        LeaderLatchListener listener = new LeaderLatchListener() {
            @SneakyThrows
            @Override
            public void isLeader() {
                log.info("我是主节点: {}", leaderLatch.getId());
                TimeUnit.SECONDS.sleep(5);
                if (leaderLatch.hasLeadership()) {
                    leaderLatch.close();
                    log.info("释放领导权");
                }
            }

            @Override
            public void notLeader() {
                log.info("我不是主节点, id={}", leaderLatch.getId());
            }
        };

        leaderLatch.addListener(listener);

        try {
            leaderLatch.start();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
