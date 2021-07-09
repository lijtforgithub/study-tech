package com.ljt.study.redis.lock.registry;

import com.ljt.study.redis.lock.LockTest;
import com.ljt.study.redis.lock.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author LiJingTang
 * @date 2021-07-09 11:38
 */
@SpringBootTest
class BootTest extends LockTest {

    @Autowired
    private OrderService orderService;

    @Test
    void grab() {
        super.test(orderService);
    }

}
