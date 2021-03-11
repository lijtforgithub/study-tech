package com.ljt.study.es;

import com.ljt.study.es.doc.TestCreateDoc;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;

/**
 * @author LiJingTang
 * @date 2020-11-13 08:55
 */
@Slf4j
@SpringBootTest
public class EsRestTemplateTest {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    public void testCreateIndex() {
        elasticsearchRestTemplate.indexOps(TestCreateDoc.class).create();
    }

    @Test
    public void testCreateMapping() {
        final IndexOperations indexOps = elasticsearchRestTemplate.indexOps(TestCreateDoc.class);
        indexOps.putMapping(indexOps.createMapping());
    }

}
