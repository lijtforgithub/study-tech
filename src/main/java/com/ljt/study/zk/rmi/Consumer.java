package com.ljt.study.zk.rmi;

import com.ljt.study.zk.ZkUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.util.Assert;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.ljt.study.zk.ZkUtils.RMI_PATH;

/**
 * @author LiJingTang
 * @date 2021-07-17 17:20
 */
@Slf4j
class Consumer {

    @SneakyThrows
    public static void main(String[] args) {
        Service service = new Consumer(ZkUtils.newInstance()).lookup();
        log.info(service.say("白雪纷纷何所似"));
    }


    private final ZooKeeper zk;
    private volatile List<String> urls = new ArrayList<>();

    private Consumer(ZooKeeper zk) {
        Assert.notNull(zk, "zk为空");
        this.zk = zk;
        watchNode();
    }

    private void watchNode() {
        try {
            List<String> nodeList = zk.getChildren(RMI_PATH, event -> {
                if (Watcher.Event.EventType.NodeChildrenChanged == event.getType()) {
                    watchNode();
                }
            });

            List<String> list = new ArrayList<>();

            for (String node : nodeList) {
                byte[] data = zk.getData(RMI_PATH + "/" + node, false, null);
                list.add(new String(data));
            }

            log.info("node data: {}", list);
            this.urls = list;
        } catch (KeeperException | InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    private <T extends Remote> T lookup() {
        T service = null;
        int size = this.urls.size();

        if (size > 0) {
            String url;

            if (1 == size) {
                url = this.urls.get(0);
                log.debug("using only url：{}", url);
            } else {
                url = this.urls.get(ThreadLocalRandom.current().nextInt(size));
                log.debug("using random url：{}", url);
            }
            log.info(url);

            try {
                service = (T) Naming.lookup(url);
            } catch (MalformedURLException | RemoteException | NotBoundException e) {
                log.error(e.getMessage());
            }
        }

        return service;
    }

}
