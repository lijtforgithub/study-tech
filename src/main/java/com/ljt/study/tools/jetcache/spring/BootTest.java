package com.ljt.study.tools.jetcache.spring;

import com.alicp.jetcache.Cache;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2021-12-13 16:23
 */
@SpringBootTest
class BootTest {

    @Autowired
    private CreateCacheBean createCacheBean;
    @Autowired
    private UserService userService;

    @SneakyThrows
    @Test
    void createCache() {
        Cache<Integer, String> cache = createCacheBean.getCache();
        System.out.println(cache.get(1));
        cache.put(1, RandomStringUtils.randomAlphabetic(1), 1, TimeUnit.MINUTES);
        System.out.println(cache.get(1));
    }

    @Test
    void cached() {
        System.out.println(userService.getUserById(1));
        System.out.println(userService.getUserById(1));
        System.out.println(userService.getUserById(2));
    }

}
