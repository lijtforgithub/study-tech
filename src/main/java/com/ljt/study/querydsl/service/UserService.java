package com.ljt.study.querydsl.service;

import com.ljt.study.querydsl.entity.User;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

import static com.ljt.study.querydsl.query.QUser.Q_USER;

/**
 * @author LiJingTang
 * @date 2021-12-06 10:46
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private SQLQueryFactory sqlQueryFactory;

    public Long save(User user) {
        Assert.notNull(user, "User对象为空");
        // 如果不需要返回主键ID 可以直接使用 execute() 方法
        return sqlQueryFactory.insert(Q_USER).populate(user).executeWithKey(Q_USER.id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(List<User> userList) {
        Assert.notEmpty(userList, "User对象集合为空");
        SQLInsertClause sqlInsertClause = sqlQueryFactory.insert(Q_USER);
        userList.forEach(user -> sqlInsertClause.populate(user).addBatch());
        sqlInsertClause.execute();
    }

    public void update(User user) {
        Assert.notNull(user, "User对象为空");
        sqlQueryFactory.update(Q_USER).populate(user).where(Q_USER.id.eq(user.getId())).execute();
    }

    public void delete(Long id) {
        Assert.notNull(id, "ID为空");
        sqlQueryFactory.delete(Q_USER).where(Q_USER.id.eq(id)).execute();
    }

    public User getOne(Long id) {
        if (Objects.isNull(id)) {
            return null;
        }

        return sqlQueryFactory.selectFrom(Q_USER).where(Q_USER.id.eq(id)).fetchOne();
    }

    public List<User> findAll() {
        return sqlQueryFactory.selectFrom(Q_USER).fetch();
    }

}
