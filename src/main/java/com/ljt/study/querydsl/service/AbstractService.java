package com.ljt.study.querydsl.service;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQueryFactory;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2021-12-09 20:16
 */
public abstract class AbstractService<E, I extends Comparable<I>, Q extends ComparableExpressionBase<I> & Path<I>> implements BaseService<E, I> {

    @Autowired
    private SQLQueryFactory sqlQueryFactory;

    protected RelationalPathBase<E> qEntity;
    protected Q qId;

    protected AbstractService(RelationalPathBase<E> qEntity, Q qId) {
        this.qEntity = qEntity;
        this.qId = qId;
    }

    @Override
    public I insert(E e) {
        checkObj(e);
        return sqlQueryFactory.insert(qEntity).populate(e).executeWithKey(qId);
    }

    @Override
    public long update(I id, E e) {
        checkId(id);
        checkObj(e);
        return sqlQueryFactory.update(qEntity).populate(e).where(qId.eq(id)).execute();
    }

    @Override
    public long delete(I id) {
        checkId(id);
        return sqlQueryFactory.delete(qEntity).where(qId.eq(id)).execute();
    }

    @Override
    public E getOne(I id) {
        if (Objects.isNull(id)) {
            return null;
        }
        return sqlQueryFactory.selectFrom(qEntity).where(qId.eq(id)).fetchOne();
    }

    @Override
    @SneakyThrows
    public List<E> findByParam(Object param) {
        if (Objects.isNull(param)) {
            return findAll();
        }

        List<Predicate> list = convertParam(param);
        return sqlQueryFactory.selectFrom(qEntity).where(list.toArray(new Predicate[0])).fetch();
    }

    @NotNull
    private List<Predicate> convertParam(Object param) throws IllegalAccessException, InvocationTargetException {
        List<Predicate> list = new ArrayList<>();

        for (Path<?> column : qEntity.getColumns()) {
            String field = column.getMetadata().getName();
            PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(param.getClass(), field);
            if (Objects.nonNull(pd)) {
                Object value = pd.getReadMethod().invoke(param);
                if (Objects.nonNull(value)) {
                    if (column instanceof StringPath) {
                        list.add(((StringPath) column).containsIgnoreCase(value.toString()));
                    } else if (column instanceof NumberPath) {
                        list.add(((NumberPath) column).eq(value));
                    }
                }
            }
        }

        return list;
    }

    @Override
    public List<E> findAll() {
        return sqlQueryFactory.selectFrom(qEntity).fetch();
    }

    private void checkId(I id) {
        Objects.requireNonNull(id, "ID为空");
    }

    private void checkObj(E e) {
        Objects.requireNonNull(e, "对象为空");
    }

}

