package com.ljt.study.drools;

import cn.hutool.core.io.IoUtil;
import com.ljt.study.drools.config.DynamicRuleManage;
import com.ljt.study.drools.entity.Test1;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author LiJingTang
 * @date 2023-05-06 17:55
 */
@Slf4j
@SpringBootTest
class DroolsBootTest {

    @Autowired(required = false)
    private KieBase kieBase;
    @Autowired
    private DynamicRuleManage dynamicRuleManage;


    @Test
    void test1() {
        KieSession kieSession = kieBase.newKieSession();
        Test1 t1 = new Test1();
        t1.setNum(2);
        kieSession.insert(t1);
        kieSession.fireAllRules();
        kieSession.dispose();
        System.out.println(t1);
    }

    @Test
    void load() throws IOException {
        ClassPathResource resource = new ClassPathResource("drools/test.drl");
        InputStream inputStream = resource.getInputStream();
        String content = IoUtil.read(inputStream, Charset.defaultCharset());

        KieSession kieSession = dynamicRuleManage.loadRule("xxoo.drl", content).newKieSession();
        Test1 t1 = new Test1();
        t1.setNum(2);
        kieSession.insert(t1);
        System.out.println(kieSession.fireAllRules());
        kieSession.dispose();
    }

}
