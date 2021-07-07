package com.ljt.study.es;

import com.ljt.study.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * @author LiJingTang
 * @date 2020-11-12 19:29
 */
@SpringBootApplication
@PropertySource(value = "classpath:elk/application.yml", factory = YamlPropertySourceFactory.class)
class EsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsApplication.class);
    }

}
