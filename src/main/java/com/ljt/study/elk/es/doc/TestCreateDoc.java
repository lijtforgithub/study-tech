package com.ljt.study.elk.es.doc;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author LiJingTang
 * @date 2020-11-13 09:13
 */
@Data
@Document(indexName = "test_create", shards = 3)
public class TestCreateDoc {

    @Id
    private Long id;
    @Field(type = FieldType.Keyword)
    private String name;
    @Field(type = FieldType.Auto)
    private String desc;

}
