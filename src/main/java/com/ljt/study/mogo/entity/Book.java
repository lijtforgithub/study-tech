package com.ljt.study.mogo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author LiJingTang
 * @date 2022-11-30 18:06
 */
@Document("book")
@Data
public class Book {

    @Id
    private Long id;
    private String title;
    private Integer page;

}
