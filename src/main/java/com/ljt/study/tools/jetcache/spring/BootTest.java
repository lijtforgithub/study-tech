package com.ljt.study.tools.jetcache.spring;

import com.alicp.jetcache.Cache;
import com.ljt.study.querydsl.entity.User;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
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

    @SneakyThrows
    @Test
    void getUserById() {
        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(new Thread(() -> userService.getUserById(1)));
        }
        list.forEach(Thread::start);

        TimeUnit.SECONDS.sleep(30);
        System.out.println(userService.getUserById(1));
    }

    @Test
    void updateUser() {
        System.out.println(userService.getUserById(1));

        User user = new User();
        user.setId(1L);
        user.setName("更新缓存");
        userService.updateUser(user);

        System.out.println(userService.getUserById(1));
    }

    @Test
    void deleteUser() {
        System.out.println(userService.getUserById(1));
        userService.deleteUser(1);
    }

}
