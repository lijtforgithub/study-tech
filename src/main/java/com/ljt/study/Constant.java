package com.ljt.study;

/**
 * @author LiJingTang
 * @date 2020-01-03 20:49
 */
public class Constant {

    private Constant() {
    }

    public static final String LOCAL_HOST = "localhost";

    public static final String DESKTOP = "/Users/lijingtang/Downloads/";

    public static final String XLSX = ".xlsx";


    /**
     * redis
     */
    public static final int REDIS_DEF_PORT = 6379;
    public static final String REDIS_TEST_KEY_PREFIX = "lijt:test:";

    /**
     * zookeeper
     */
    public static final String ZK_SERVER = LOCAL_HOST + ":2181";
    public static final int ZK_TIMEOUT = 5000;

}
