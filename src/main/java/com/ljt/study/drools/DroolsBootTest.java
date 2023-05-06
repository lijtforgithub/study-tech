package com.ljt.study.drools;

import com.ljt.study.drools.entity.Test1;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author LiJingTang
 * @date 2023-05-06 17:55
 */
@SpringBootTest
class DroolsBootTest {

    @Autowired
    private KieBase kieBase;

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

}
