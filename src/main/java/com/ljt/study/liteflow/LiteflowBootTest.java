package com.ljt.study.liteflow;

import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author LiJingTang
 * @date 2023-05-12 15:24
 */
@SpringBootTest
class LiteflowBootTest {

    @Resource
    private FlowExecutor flowExecutor;

    @Test
    void test () {
        LiteflowResponse response = flowExecutor.execute2Resp("test", "arg");
    }

}
