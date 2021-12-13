package com.ljt.study.querydsl;

import com.ljt.study.dynamicds.DynamicDataSource;
import com.ljt.study.querydsl.entity.User;
import com.ljt.study.querydsl.service.UserService;
import com.ljt.study.querydsl.service.UserServiceImpl;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ljt.study.querydsl.query.QUser.Q_USER;

/**
 * @author LiJingTang
 * @date 2021-12-06 11:20
 */

@SpringBootTest
class QdslTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserServiceImpl userServiceImpl;

    private static User newUser() {
        User user = new User();
        user.setName(String.format("QueryDSL：%d", new Random().nextInt(1000)));
        return user;
    }

    @Test
    void getOne() {
        DynamicDataSource.set("DynamicDataSource[test]");
        System.out.println(userService.getOne(1L));
    }

    @Test
    void findAll() {
        System.out.println(userService.findAll());
    }

    @Test
    void save() {
        System.out.println(userService.save(newUser()));
    }

    @Test
    void saveList() {
        List<User> users = IntStream.range(1, 10).mapToObj(i -> newUser()).collect(Collectors.toList());
        userService.save(users);
    }

    @Test
    void update() {
        User user = new User();
        user.setId(1L);
        user.setName("XXXX");
        userService.update(user);
    }

    @Test
    void delete() {
        userService.delete(1L);
    }

    @Test
    void test() {
        System.out.println(Q_USER.getType().getSimpleName());
        System.out.println(userServiceImpl.findAll());
    }


    @Autowired
    private StringEncryptor stringEncryptor;

    @Test
    void jasypt() {
        String encrypt = stringEncryptor.encrypt("admin");
        System.out.println(encrypt);
        System.out.println(stringEncryptor.decrypt(encrypt));
    }

}
