package com.ljt.study.quartz.plan.entity;

import lombok.Data;

import java.time.LocalTime;
import java.util.Date;

/**
 * @author LiJingTang
 * @date 2023-03-08 14:20
 */
@Data
public class JobPlan {

    private String name;
    private Date startTime;
    private Date endTime;
    private Integer planType;
    private Integer planOption;

    private Date onceExeTime;
    private LocalTime cycleExeTime;

    private Date lastTime;
    private Date nextTime;

}
