package com.ljt.study.querydsl.query;

import com.ljt.study.querydsl.entity.User;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.PrimaryKey;
import com.querydsl.sql.RelationalPathBase;

import java.sql.Types;

/**
 * @author LiJingTang
 * @date 2021-12-06 10:57
 */
public class QUser extends RelationalPathBase<User> {

    private static final long serialVersionUID = 6700639011101220045L;
    public static final QUser Q_USER = new QUser("u");

    public final NumberPath<Long> id = createNumber("id", Long.class);
    public final StringPath name = createString("name");

    public final PrimaryKey<User> pk = createPrimaryKey(id);

    /**
     * @param variable 表的别名
     */
    public QUser(String variable) {
        super(User.class, variable, null, "user");
        addMeta();
    }

    public void addMeta() {
        addMetadata(id, ColumnMetadata.named("id").ofType(Types.BIGINT));
        addMetadata(name, ColumnMetadata.named("name").ofType(Types.VARCHAR));
    }

}
