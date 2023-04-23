package com.ljt.study.kafka.spring;

import com.ljt.study.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * @author LiJingTang
 * @date 2023-04-20 15:35
 */
@SpringBootApplication
@PropertySource(value = "classpath:kafka/application.yml", factory = YamlPropertySourceFactory.class)
class KafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class);
    }

}
