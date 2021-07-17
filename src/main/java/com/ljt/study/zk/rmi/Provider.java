package com.ljt.study.zk.rmi;

import com.ljt.study.zk.ZkUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.util.Assert;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import static com.ljt.study.Constant.LOCAL_HOST;
import static com.ljt.study.zk.ZkUtils.RMI_PATH;
import static com.ljt.study.zk.ZkUtils.RMI_SERVICE_PATH;

/**
 * @author LiJingTang
 * @date 2021-07-17 17:17
 */
@Slf4j
class Provider {

    @SneakyThrows
    public static void main(String[] args) {
        new Provider(ZkUtils.newInstance()).resister(new ServiceImpl(), LOCAL_HOST, 6668);
    }


    private final ZooKeeper zk;

    private Provider(ZooKeeper zk) {
        Assert.notNull(zk, "zk为空");
        this.zk = zk;
    }

    @SneakyThrows
    private void resister(Remote service, String host, int port) {
        String url = publish(service, host, port);

        if (url != null) {
            ZkUtils.createRootPath(zk, RMI_PATH);
            createNode(zk, url);
        }
    }

    private void createNode(ZooKeeper zk, String url) {
        try {
            byte[] data = url.getBytes();
            String path = zk.create(RMI_SERVICE_PATH, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            log.info("create zookeeper node ({} => {})", path, url);
        } catch (KeeperException | InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    private String publish(Remote service, String host, int port) {
        String url = null;

        try {
            url = String.format("rmi://%s:%d/%s", host, port, service.getClass().getName());
            LocateRegistry.createRegistry(port);
            Naming.bind(url, service);
            log.info("publish rmi service (url:{})", url);
        } catch (RemoteException | MalformedURLException | AlreadyBoundException e) {
            log.error(e.getMessage());
        }

        return url;
    }

}
