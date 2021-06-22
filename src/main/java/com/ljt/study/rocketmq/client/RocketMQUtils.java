package com.ljt.study.rocketmq.client;

import static com.ljt.study.Constant.LOCAL_HOST;

/**
 * @author LiJingTang
 * @date 2021-06-19 09:57
 */
class RocketMQUtils {

    private RocketMQUtils() {}

    static final String NAME_SERVER = LOCAL_HOST + ":9876";

    static final String TEST_CLIENT_GROUP = "test_client_group";

    static final String TEST_CLIENT_TOPIC = "test_client_topic";

}
