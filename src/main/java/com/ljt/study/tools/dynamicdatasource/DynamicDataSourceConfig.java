package com.ljt.study.tools.dynamicdatasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceWrapper;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author LiJingTang
 * @date 2021-12-08 9:15
 */
@Slf4j
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@ConditionalOnProperty(name = DynamicDataSourceProperties.ENABLE, havingValue = "true")
public class DynamicDataSourceConfig {

    @Autowired
    private DynamicDataSourceProperties dynamicDataSourceProperties;

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    public DataSource dataSource() {
        log.info("Init DruidDataSource");
        return new DruidDataSourceWrapper();
    }

    @Bean(DynamicDataSource.DYNAMIC_DATASOURCE)
    public DynamicDataSource dynamicDataSource(DefaultListableBeanFactory beanFactory, DataSource dataSource) {
        if (dataSource instanceof DruidDataSource) {
            DruidDataSource ds = (DruidDataSource) dataSource;
            log.info("默认数据源：{} | {}", ds.getUrl(), ds.getValidationQuery());
        }

        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setDefaultTargetDataSource(dataSource);
        dynamicDataSource.setTargetDataSources(registerDynamicDataSource(beanFactory, dataSource));
        return dynamicDataSource;
    }

    @SneakyThrows
    private Map<Object, Object> registerDynamicDataSource(DefaultListableBeanFactory beanFactory, DataSource dataSource) {
        Map<Object, Object> dataSources = Maps.newHashMapWithExpectedSize(dynamicDataSourceProperties.getDataSources().size());

        for (DataSourceProperties prop : dynamicDataSourceProperties.getDataSources()) {
            String dbName = prop.getUrl().substring(prop.getUrl().lastIndexOf("/") + 1, Math.max(prop.getUrl().length(), prop.getUrl().indexOf("?")));
            String beanName = "DynamicDataSource[" + dbName + "]";

            if (!beanFactory.containsBean(beanName)) {
                /*
                 * HikariDataSource 实例化方式
                 * DataSource dataSource = tmpProperties.initializeDataSourceBuilder().build()
                 * ((HikariDataSource) dataSource).setPoolName(beanName)
                 */
                DruidDataSourceWrapper tmp = new DruidDataSourceWrapper();
                BeanUtils.copyProperties(dataSource, tmp);
                tmp.setUrl(prop.getUrl());
                if (StringUtils.isNotBlank(prop.getUsername())) {
                    tmp.setUsername(prop.getUsername());
                }
                if (StringUtils.isNotBlank(prop.getPassword())) {
                    tmp.setPassword(prop.getPassword());
                }

                tmp.init();
                beanFactory.registerSingleton(beanName, tmp);
            }

            dataSources.putIfAbsent(beanName, beanFactory.getBean(beanName));
        }

        log.info("动态数据源: {}", dataSources.keySet());
        return dataSources;
    }

}
