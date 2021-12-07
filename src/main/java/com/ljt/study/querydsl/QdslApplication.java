package com.ljt.study.querydsl;

import com.ljt.study.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * @author LiJingTang
 * @date 2021-12-06 10:45
 */
@SpringBootApplication
@PropertySource(value = "classpath:querydsl/application.yml", factory = YamlPropertySourceFactory.class)
class QdslApplication {

    public static void main(String[] args) {
        SpringApplication.run(QdslApplication.class, args);
    }

}
