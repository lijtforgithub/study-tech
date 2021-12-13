package com.ljt.study.querydsl.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LiJingTang
 * @date 2021-12-06 10:51
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 8024238473953454434L;

    private Long id;
    private String name;

}
