package com.ljt.study.elk.es;

import com.alibaba.fastjson.JSON;
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
        doc.setId(4L);
        doc.setName("小米");
        doc.setDesc("HuaWei phone");
        doc.setTags("为发烧而生");
        doc.setPrice(2999);
        doc.setCreateDate("2020-11-18");
        testDocService.save(doc);
    }

    @Test
    public void testFindAll() {
        log.info(JSON.toJSONString(testDocService.findAll()));
    }

    @Test
    public void testFindByName() {
        log.info(JSON.toJSONString(testDocService.findByName("filter")));
    }

    @Test
    public void testFindByCreateDate() {
        log.info(JSON.toJSONString(testDocService.findByCreateDate("2020-11-18")));
    }

}
