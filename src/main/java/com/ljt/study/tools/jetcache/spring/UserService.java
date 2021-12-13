package com.ljt.study.tools.jetcache.spring;

import com.alicp.jetcache.anno.Cached;
import com.ljt.study.querydsl.entity.User;

import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2021-12-13 16:23
 */
public interface UserService {

    @Cached(name="UserService.getUserById_", expire = 1, timeUnit = TimeUnit.MINUTES)
    User getUserById(int id);

}
