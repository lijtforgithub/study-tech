package com.ljt.study.zk.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.*;

import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2021-07-18 16:21
 */
@Slf4j
class LeaderSelectorTask implements Runnable {

    private final CuratorFramework client;
    private final String path;

    LeaderSelectorTask(CuratorFramework client, String path) {
        this.client = client;
        this.path = path;
    }

    @Override
    public void run() {
        LeaderSelectorListener listener = new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework client) throws InterruptedException {
                // 当实例被选为leader之后，调用takeLeadership方法进行业务逻辑处理，处理完成即释放领导权
                log.info("成为 leader");
                TimeUnit.SECONDS.sleep(5);
                log.info("takeLeadership方法结束：释放领导权");
            }
        };

        LeaderSelector selector = new LeaderSelector(client, path, listener);
        // 确保此实例在释放领导权后还可能获得领导权
        selector.autoRequeue();
        selector.start();
    }

}
