package com.ljt.study.dynamicds;

import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

import static com.ljt.study.dynamicds.DynamicDataSourceProperties.PREFIX;

/**
 * @author LiJingTang
 * @date 2021-12-08 11:10
 */
@Data
@ConfigurationProperties(prefix = PREFIX)
public class DynamicDataSourceProperties {

    static final String PREFIX = "spring.datasource.dynamic";
    static final String ENABLE = PREFIX + ".enable";

    private boolean enable = false;

    private List<DataSourceProperties> dataSources = new ArrayList<>();

}
