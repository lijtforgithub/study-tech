package com.ljt.study.elk.es;

import com.ljt.study.elk.es.doc.TestDoc;
import com.ljt.study.elk.es.service.TestDocService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author LiJingTang
 * @date 2020-11-17 09:59
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestDocServiceTest {

    @Autowired
    private TestDocService testDocService;

    @Test
    public void testSave() {
        TestDoc doc = new TestDoc();
        doc.setId(1L);
        doc.setName("mobile");
        doc.setDesc("apple phone");
        doc.setTags("智能手机");
        doc.setPrice(5999);
        doc.setCreateDate("2020-11-17");
        testDocService.save(doc);
    }

}
