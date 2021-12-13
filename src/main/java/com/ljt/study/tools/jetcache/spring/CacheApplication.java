package com.ljt.study.tools.jetcache.spring;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.ljt.study.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * @author LiJingTang
 * @date 2021-12-13 15:30
 */
@SpringBootApplication
@EnableMethodCache(basePackages = "com.ljt.study.tools.jetcache.spring")
@EnableCreateCacheAnnotation
@PropertySource(value = "classpath:jetcache/application.yml", factory = YamlPropertySourceFactory.class)
class CacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheApplication.class);
    }

}
