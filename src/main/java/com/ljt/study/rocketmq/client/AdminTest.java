package com.ljt.study.rocketmq.client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.protocol.body.ClusterInfo;
import org.apache.rocketmq.common.protocol.route.TopicRouteData;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.ljt.study.rocketmq.client.RocketMQUtils.NAME_SERVER;

/**
 * @author LiJingTang
 * @date 2022-05-04 9:21
 */
@Slf4j
class AdminTest {

    private static DefaultMQAdminExt admin;

    @BeforeAll
    @SneakyThrows
    static void before() {
         admin = new DefaultMQAdminExt();
         admin.setNamesrvAddr(NAME_SERVER);
         admin.start();
    }

    @SneakyThrows
    @Test
    void testCluster() {
        ClusterInfo clusterInfo = admin.examineBrokerClusterInfo();
        log.info("集群信息\n");
        clusterInfo.getClusterAddrTable().forEach((k, v) -> log.info("{} -> {}", k, v));
    }

    @SneakyThrows
    @Test
    void testTopic() {
        admin.fetchAllTopicList().getTopicList().forEach(topic -> {
            try {
                TopicRouteData routeData = admin.examineTopicRouteInfo(topic);
                log.info("{} -> {}", topic, routeData.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
