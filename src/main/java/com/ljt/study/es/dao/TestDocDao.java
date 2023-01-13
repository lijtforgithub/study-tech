package com.ljt.study.es.dao;

import com.ljt.study.es.entity.TestDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author LiJingTang
 * @date 2020-11-17 09:53
 */
public interface TestDocDao extends ElasticsearchRepository<TestDoc, Long> {
}
