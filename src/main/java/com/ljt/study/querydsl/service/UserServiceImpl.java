package com.ljt.study.querydsl.service;

import com.ljt.study.querydsl.entity.User;
import com.querydsl.core.types.dsl.NumberPath;
import org.springframework.stereotype.Service;

import static com.ljt.study.querydsl.query.QUser.Q_USER;

/**
 * @author LiJingTang
 * @date 2021-12-09 20:21
 */
@Service
public class UserServiceImpl extends AbstractService<User, Long, NumberPath<Long>>{

    public UserServiceImpl() {
        super(Q_USER, Q_USER.id);
    }

}
