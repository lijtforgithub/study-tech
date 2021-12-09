package com.ljt.study.querydsl.service;

import java.util.List;

/**
 * @author LiJingTang
 * @date 2021-12-09 20:17
 */
public interface BaseService<E, I> {

    I insert(E e);

    long update(I id, E e);

    long delete(I id);

    E getOne(I id);

    List<E> findByParam(Object param);

    List<E> findAll();
    
}
