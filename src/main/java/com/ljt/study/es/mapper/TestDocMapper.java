package com.ljt.study.es.mapper;

import com.ljt.study.es.doc.TestDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author LiJingTang
 * @date 2020-11-17 09:53
 */
public interface TestDocMapper extends ElasticsearchRepository<TestDoc, Long> {
}
