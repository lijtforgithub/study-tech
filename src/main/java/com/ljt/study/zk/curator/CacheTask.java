package com.ljt.study.zk.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

import java.util.Objects;


/**
 * Node Cache：（监听和缓存根节点变化） 只监听单一个节点(变化 添加，修改，删除)
 * Path Cache：（监听和缓存子节点变化） 监听这个节点下的所有子节点(变化 添加，修改，删除)
 *
 * @author LiJingTang
 * @date 2021-07-18 17:49
 */
@Slf4j
class CacheTask {

    private final CuratorFramework client;
    private final String path;
    private CuratorCache curatorCache;

    CacheTask(CuratorFramework client, String path) {
        this.client = client;
        this.path = path;
    }

    /**
     * 监听数据节点的变化情况
     */
    CacheTask nodeCache() {
        curatorCache = CuratorCache.build(client, path, CuratorCache.Options.SINGLE_NODE_CACHE);
        curatorCache.start();

        CuratorCacheListener listener = CuratorCacheListener.builder()
                .forNodeCache(() -> log.info("nodeCache: nodeChanged")).build();
        curatorCache.listenable().addListener(listener);

        addListener();
        return this;
    }

    private void addListener() {
        curatorCache.listenable().addListener((type, oldData, data) -> log.info("type={} \n oldData={} \n newData={}",
                type, getData(oldData), getData(data)));
    }

    /**
     * 监听子节点的变化情况
     */
    CacheTask pathCache() {
        curatorCache = CuratorCache.builder(client, path).build();
        curatorCache.start();

        CuratorCacheListener listener = CuratorCacheListener.builder()
                .forPathChildrenCache(path, client, (client, event) -> log.info("pathCache: {}", event.getType())).build();
        curatorCache.listenable().addListener(listener);

        addListener();
        return this;
    }

    void close() {
        if (Objects.nonNull(curatorCache)) {
            curatorCache.close();
        }
    }

    void printData() {
        if (Objects.nonNull(curatorCache)) {
            log.info("size={}", curatorCache.size());
            curatorCache.stream().forEach(this::getData);
        }
    }

    private String getData(ChildData data) {
        if (Objects.isNull(data)) {
            return null;
        }

        return data.getPath() + " : " + new String(data.getData());
    }

}
