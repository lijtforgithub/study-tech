package com.ljt.study.zk;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2019-11-28 15:18
 */
public class RMITest {

    public static void main(String[] args) {

    }

    private static class Server implements Runnable {

        @Override
        public void run() {
            try {
                Service service = new ServiceImpl();
                String host = "192.168.40.19";
                int port = 6668;

                ServerProvider.resister(service, host, port);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Client implements Runnable {

        @Override
        public void run() {
            ServiceConsumer serviceConsumer = new ServiceConsumer();

            while (true) {
                Service service = serviceConsumer.lookup();
                try {
                    System.out.println(service.say("白雪纷纷何所似"));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static final String ZK_REGISTRY_PATH = "/registry";
    private static final String ZK_PROVIDER_PATH = ZK_REGISTRY_PATH + "/provider";

    private static class ServerProvider {

        private static final Logger logger = LoggerFactory.getLogger(ServerProvider.class);

        public static void resister(Remote service, String host, int port) {
            String url = publishService(service, host, port);

            if (url != null) {
                ZooKeeper zk = ZKFactory.getConnection();

                if (zk != null) {
                    createNode(zk, url);
                }
            }
        }

        private static void createNode(ZooKeeper zk, String url) {
            try {
                byte[] data = url.getBytes();
                String path = zk.create(ZK_PROVIDER_PATH, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                logger.info("create zookeeper node ({} => {})", path, url);
            } catch (KeeperException | InterruptedException e) {
                logger.error(e.getMessage());
            }
        }

        private static String publishService(Remote service, String host, int port) {
            String url = null;

            try {
                url = String.format("rmi://%s:%d/%s", host, port, service.getClass().getName());
                LocateRegistry.createRegistry(port);
                Naming.bind(url, service);
                logger.info("publish rmi service (url:{})", url);
            } catch (RemoteException | MalformedURLException | AlreadyBoundException e) {
                logger.error(e.getMessage());
            }

            return url;
        }
    }

    private static class ServiceConsumer {

        private static final Logger logger = LoggerFactory.getLogger(ServiceConsumer.class);

        private volatile List<String> urls = new ArrayList<>();

        public ServiceConsumer() {
            super();

            ZooKeeper zk = ZKFactory.getConnection();

            if (zk != null) {
                watchNode(zk);
            }
        }

        private void watchNode(final ZooKeeper zk) {
            try {
                List<String> nodeList = zk.getChildren(ZK_REGISTRY_PATH, event -> {
                    if (Watcher.Event.EventType.NodeChildrenChanged == event.getType()) {
                        watchNode(zk);
                    }
                });

                List<String> list = new ArrayList<>();

                for (String node : nodeList) {
                    byte[] data = zk.getData(ZK_REGISTRY_PATH + "/" + node, false, null);
                    list.add(new String(data));
                }

                logger.info("node data: {}", list);
                this.urls = list;
            } catch (KeeperException | InterruptedException e) {
                logger.error(e.getMessage());
            }
        }

        public <T extends Remote> T lookup() {
            T service = null;
            int size = this.urls.size();

            if (size > 0) {
                String url;

                if (1 == size) {
                    url = this.urls.get(0);
                    logger.debug("using only url：{}", url);
                } else {
                    url = this.urls.get(ThreadLocalRandom.current().nextInt(size));
                    logger.debug("using random url：{}", url);
                }
                System.out.println(url);

                try {
                    service = (T) Naming.lookup(url);
                } catch (MalformedURLException | RemoteException | NotBoundException e) {
                    logger.error(e.getMessage());
                }
            }

            return service;
        }
    }


    private interface Service extends Remote {

        String helloworld() throws RemoteException;

        String say(String name) throws RemoteException;

    }

    private static class ServiceImpl extends UnicastRemoteObject implements Service {

        private static final long serialVersionUID = 5787919934128900864L;

        protected ServiceImpl() throws RemoteException {
            super();
        }

        @Override
        public String helloworld() throws RemoteException {
            return "Hello World";
        }

        @Override
        public String say(String name) throws RemoteException {
            return "Hello " + name;
        }

    }

}
