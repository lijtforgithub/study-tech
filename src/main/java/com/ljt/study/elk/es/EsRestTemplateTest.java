package com.ljt.study.elk.es;

import com.ljt.study.elk.es.doc.SaleGoods;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author LiJingTang
 * @date 2020-11-13 08:55
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsRestTemplateTest {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    public void test() {
        elasticsearchRestTemplate.indexOps(SaleGoods.class).create();
    }

}
