package com.ljt.study.liteflow.config;

import com.ljt.study.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author LiJingTang
 * @date 2023-05-12 15:26
 */
@Configuration
@PropertySource(value = "classpath:liteflow/application.yml", factory = YamlPropertySourceFactory.class)
class MyLiteflowConfig {
}
