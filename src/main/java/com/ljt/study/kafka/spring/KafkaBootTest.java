package com.ljt.study.kafka.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author LiJingTang
 * @date 2023-04-20 15:38
 */
@SpringBootTest
class KafkaBootTest {

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @Test
    void test() {
        kafkaTemplate.send("xxoo", "kafka message");
    }

}
