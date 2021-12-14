package com.ljt.study.tools.jetcache.spring;

import com.alicp.jetcache.anno.*;
import com.ljt.study.querydsl.entity.User;

import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2021-12-13 16:23
 */
public interface UserService {

    /**
     * @CachePenetrationProtect 当缓存访问未命中的情况下，对并发进行的加载行为进行保护。 当前版本实现的是单JVM内的保护，即同一个JVM中同一个key只有一个线程去加载，其它线程等待结果。
     */
    @CachePenetrationProtect
    @CacheRefresh(refresh = 10)
    @Cached(name="userCache.", key="#id", expire = 1, timeUnit = TimeUnit.HOURS)
    User getUserById(int id);

    @CacheUpdate(name="userCache.", key="#user.id", value="#user")
    void updateUser(User user);

    @CacheInvalidate(name="userCache.", key="#id")
    void deleteUser(int id);

}
