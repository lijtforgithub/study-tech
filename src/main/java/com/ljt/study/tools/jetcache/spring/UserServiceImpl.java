package com.ljt.study.tools.jetcache.spring;

import com.ljt.study.querydsl.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

/**
 * @author LiJingTang
 * @date 2021-12-13 16:35
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Override
    public User getUserById(int id) {
        User user = new User();
        user.setId((long) id);
        user.setName(RandomStringUtils.randomAlphabetic(1));

        log.info("创建 User[{}] 对象", user);

        return user;
    }

    @Override
    public void updateUser(User user) {
        log.info("更新 User[{}] 缓存", user.getId());
    }

    @Override
    public void deleteUser(int id) {
        log.info("删除 User[{}] 缓存", id);
    }

}
