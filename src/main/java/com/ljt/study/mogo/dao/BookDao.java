package com.ljt.study.mogo.dao;

import com.ljt.study.mogo.entity.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author LiJingTang
 * @date 2022-11-30 18:07
 */
@Repository
public interface BookDao extends MongoRepository<Book, Integer> {
}
