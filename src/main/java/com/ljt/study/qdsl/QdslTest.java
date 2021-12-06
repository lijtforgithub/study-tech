package com.ljt.study.qdsl;

import com.ljt.study.qdsl.entity.User;
import com.ljt.study.qdsl.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author LiJingTang
 * @date 2021-12-06 11:20
 */

@SpringBootTest
class QdslTest {

    @Autowired
    private UserService userService;

    private static User newUser() {
        User user = new User();
        user.setName(String.format("QueryDSL：%d", new Random().nextInt(1000)));
        return user;
    }

    @Test
    void getOne() {
        System.out.println(userService.getOne(3L));
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

}
