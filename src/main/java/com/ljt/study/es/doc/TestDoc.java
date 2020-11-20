package com.ljt.study.es.doc;

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
@Document(indexName = "test_doc", shards = 3)
public class TestDoc {

    @Id
    private Long id;
    @Field(type = FieldType.Keyword)
    private String name;
    @Field(type = FieldType.Text)
    private String desc;
    @Field(type = FieldType.Date)
    private String createDate;
    @Field(type = FieldType.Text)
    private String tags;
    @Field(type = FieldType.Float)
    private double price;

}
