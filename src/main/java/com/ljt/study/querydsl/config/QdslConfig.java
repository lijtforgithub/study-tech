package com.ljt.study.querydsl.config;

import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLCloseListener;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.postgresql.PostgreSQLQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;

/**
 * @author LiJingTang
 * @date 2021-12-06 10:03
 */
@Slf4j
@Configuration
public class QdslConfig {

    /**
     * 必须使用 DataSourceUtils.getConnection(dataSource) 返回的数据库连接才支持事务
     *
     * @see PostgreSQLQueryFactory
     * @see SQLCloseListener
     */
    @Bean
    public SQLQueryFactory sqlQueryFactory(DataSource dataSource) {
        return new SQLQueryFactory(PostgreSQLTemplates.DEFAULT, () -> DataSourceUtils.getConnection(dataSource));
    }

}
