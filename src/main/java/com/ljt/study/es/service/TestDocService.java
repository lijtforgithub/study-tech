package com.ljt.study.es.service;

import com.alibaba.fastjson.JSON;
import com.ljt.study.es.doc.TestDoc;
import com.ljt.study.es.mapper.TestDocMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LiJingTang
 * @date 2020-11-17 09:57
 */
@Slf4j
@Service
public class TestDocService {

    public static final String PRICE = "price";

    @Autowired
    private TestDocMapper testDocMapper;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    public void save(TestDoc doc) {
        testDocMapper.save(doc);
    }

    public void saveList(List<TestDoc> docList) {
        List<IndexQuery> queries = docList.stream().map(doc -> {
            IndexQuery query = new IndexQuery();
            query.setId(doc.getId().toString());
            query.setSource(JSON.toJSONString(doc));
            return query;
        }).collect(Collectors.toList());
        elasticsearchRestTemplate.bulkIndex(queries, IndexCoordinates.of("test_doc"));
    }

    public List<TestDoc> findAll() {
        Page<TestDoc> page = (Page<TestDoc>) testDocMapper.findAll();
        return page.getContent();
    }

    public List<TestDoc> findByName(String name) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery("name", name));
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQuery)
                .withSort(SortBuilders.fieldSort(PRICE).order(SortOrder.ASC)).build();
        SearchHits<TestDoc> searchHits = elasticsearchRestTemplate.search(searchQuery, TestDoc.class);
        return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    public List<TestDoc> findByCreateDate(String createDate) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery("createDate", createDate));
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQuery)
                .withSort(SortBuilders.fieldSort(PRICE).order(SortOrder.DESC)).build();
        SearchHits<TestDoc> searchHits = elasticsearchRestTemplate.search(searchQuery, TestDoc.class);
        log.info(JSON.toJSONString(searchHits));
        return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

}
