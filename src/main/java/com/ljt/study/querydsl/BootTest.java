package com.ljt.study.querydsl;

import com.ljt.study.querydsl.service.UserServiceImpl;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.ljt.study.querydsl.query.QUser.Q_USER;

/**
 * @author LiJingTang
 * @date 2021-12-13 15:23
 */
@SpringBootTest
class BootTest {

    @Autowired
    private StringEncryptor stringEncryptor;
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Test
    void test() {
        System.out.println(Q_USER.getType().getSimpleName());
        System.out.println(userServiceImpl.findAll());
    }

    @Test
    void jasypt() {
        String encrypt = stringEncryptor.encrypt("admin");
        System.out.println(encrypt);
        System.out.println(stringEncryptor.decrypt(encrypt));
    }

}
