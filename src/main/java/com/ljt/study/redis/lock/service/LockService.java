package com.ljt.study.redis.lock.service;

import java.util.concurrent.locks.Lock;

/**
 * @author LiJingTang
 * @date 2021-07-09 11:10
 */
public interface LockService {

    Lock getLock(String name);

}
