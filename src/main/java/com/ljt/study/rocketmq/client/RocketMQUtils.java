package com.ljt.study.rocketmq.client;

/**
 * @author LiJingTang
 * @date 2021-06-19 09:57
 */
class RocketMQUtils {

    private RocketMQUtils() {}

//    static final String NAME_SERVER = LOCAL_HOST + ":9876";
    static final String NAME_SERVER = "127.0.0.1:9876";

    static final String CLIENT_TOPIC = "test_client_topic";
    static final String CLIENT_GROUP = "test_client_group";
    static final String CLIENT_GROUP_X = "test_client_group_x";


    static final String DEF_TOPIC = "test_client_topic_def";
    static final String DEF_GROUP = "test_client_group_def";

}
