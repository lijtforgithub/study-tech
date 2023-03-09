package com.ljt.study.quartz.plan.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LiJingTang
 * @date 2023-03-08 10:55
 */
@AllArgsConstructor
@Getter
public enum PlanEnum {

    DATE_TIME(0, "明确时间"),
    DAILY(1, "每日"),
    WEEK(7, "每周"),
    MONTH(30, "每月");

    private final int value;
    private final String desc;

}
