package com.ljt.study.elk.es.doc;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author LiJingTang
 * @date 2020-11-13 09:13
 */
@Data
@Document(indexName = "sale-goods", shards = 3)
public class SaleGoods {

    @Id
    private Long id;
    @Field
    private String name;
    @Field
    private String desc;

}
