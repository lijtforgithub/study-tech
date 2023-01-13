package com.ljt.study.mogo;

import com.ljt.study.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * @author LiJingTang
 * @date 2022-11-30 18:47
 */
@PropertySource(value = "classpath:mogo/application.yml", factory = YamlPropertySourceFactory.class)
@SpringBootApplication
class MogoBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(MogoBootApplication.class);
    }

}
