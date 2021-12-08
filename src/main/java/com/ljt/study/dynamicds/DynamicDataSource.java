package com.ljt.study.dynamicds;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2021-12-08 9:16
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    static final String DYNAMIC_DATASOURCE = "dynamicDataSource";

    private static final ThreadLocal<String> CONTEXT_HOLDER = new InheritableThreadLocal<>();

    @Override
    protected Object determineCurrentLookupKey() {
        return get();
    }

    public static void set(String dataSource) {
        if (Objects.isNull(dataSource)) {
            remove();
            return;
        }

        CONTEXT_HOLDER.set(dataSource);
    }

    public static String get() {
        return CONTEXT_HOLDER.get();
    }

    public static void remove() {
        CONTEXT_HOLDER.remove();
    }

}
