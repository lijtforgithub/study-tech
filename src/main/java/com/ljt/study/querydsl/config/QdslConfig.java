package com.ljt.study.querydsl.config;

import com.ljt.study.tools.dynamicdatasource.DynamicDataSource;
import com.ljt.study.tools.dynamicdatasource.DynamicDataSourceConfig;
import com.ljt.study.tools.encrypt.CustomEncryptablePropertyResolver;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLCloseListener;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.postgresql.PostgreSQLQueryFactory;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;

/**
 * @author LiJingTang
 * @date 2021-12-06 10:03
 */
@Slf4j
@Import(DynamicDataSourceConfig.class)
@Configuration
public class QdslConfig {

    @Bean
    @ConditionalOnMissingBean(DynamicDataSource.class)
    public SQLQueryFactory sqlQueryFactory(DataSource dataSource) {
        return getSqlQueryFactory(dataSource);
    }

    /**
     * 必须使用 DataSourceUtils.getConnection(dataSource) 返回的数据库连接才支持事务
     *
     * @see PostgreSQLQueryFactory
     * @see SQLCloseListener
     */
    @NotNull
    private SQLQueryFactory getSqlQueryFactory(DataSource dataSource) {
        log.info("sqlQueryFactory 设置数据源：{}", dataSource.getClass());
        return new SQLQueryFactory(PostgreSQLTemplates.DEFAULT, () -> DataSourceUtils.getConnection(dataSource));
    }

    @Bean
    @ConditionalOnBean(DynamicDataSource.class)
    public SQLQueryFactory sqlQueryFactoryDynamic(DynamicDataSource dataSource) {
        return getSqlQueryFactory(dataSource);
    }

    @Bean
    public EncryptablePropertyResolver encryptablePropertyResolver() {
        return new CustomEncryptablePropertyResolver();
    }

}
