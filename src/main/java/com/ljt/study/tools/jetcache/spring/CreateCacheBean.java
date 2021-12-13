package com.ljt.study.tools.jetcache.spring;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CreateCache;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2021-12-13 16:20
 */
@Getter
@Component
public class CreateCacheBean {

    @CreateCache(name="CreateCacheBean_", expire = 1, timeUnit = TimeUnit.MINUTES)
    private Cache<Integer, String> cache;

}
