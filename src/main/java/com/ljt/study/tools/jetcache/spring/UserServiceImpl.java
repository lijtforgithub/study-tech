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
        log.info("创建 User[{}] 对象", id);
        User user = new User();
        user.setId((long) id);
        user.setName(RandomStringUtils.randomAlphabetic(1));

        return user;
    }

}
