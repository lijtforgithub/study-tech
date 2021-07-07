package com.ljt.study.es;

import com.alibaba.fastjson.JSON;
import com.ljt.study.es.doc.TestDoc;
import com.ljt.study.es.service.TestDocService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author LiJingTang
 * @date 2020-11-17 09:59
 */
@Slf4j
@SpringBootTest
class DocServiceTest {

    @Autowired
    private TestDocService testDocService;

    @Test
    void testSave() {
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
    void testSaveList() {
        TestDoc doc1 = new TestDoc();
        doc1.setId(5L);
        doc1.setName("小米");
        doc1.setDesc("xiaomi phone");
        doc1.setTags("为发烧而生");
        doc1.setPrice(2999);
        doc1.setCreateDate("2020-11-19");
        TestDoc doc2 = new TestDoc();
        doc2.setId(6L);
        doc2.setName("红米");
        doc2.setDesc("hongmi phone");
        doc2.setTags("低端机");
        doc2.setPrice(1999);
        doc2.setCreateDate("2020-11-19");
        testDocService.saveList(Lists.newArrayList(doc1, doc2));
    }

    @Test
    void testFindAll() {
        log.info(JSON.toJSONString(testDocService.findAll()));
    }

    @Test
    void testFindByName() {
        log.info(JSON.toJSONString(testDocService.findByName("filter")));
    }

    @Test
    void testFindByCreateDate() {
        log.info(JSON.toJSONString(testDocService.findByCreateDate("2020-11-18")));
    }

}
