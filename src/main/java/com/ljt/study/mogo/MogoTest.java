package com.ljt.study.mogo;

import com.ljt.study.mogo.dao.BookDao;
import com.ljt.study.mogo.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author LiJingTang
 * @date 2022-11-30 18:48
 */
@SpringBootTest
class MogoTest {

    @Autowired
    private BookDao bookDao;

    @Test
    void test() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("深入理解JVM");
        book.setPage(700);
        bookDao.save(book);
    }

}
