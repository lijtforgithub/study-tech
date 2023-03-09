package com.ljt.study.quartz.plan.service.impl;

import com.ljt.study.quartz.plan.entity.JobPlan;
import com.ljt.study.quartz.plan.enums.PlanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2023-03-08 14:45
 */
@Slf4j
@Service
public class DateTimePlanServiceImpl extends AbstractPlanService {

    private static final long WIN_TIME = 1000 * 30;

    @Override
    public PlanEnum getType() {
        return PlanEnum.DATE_TIME;
    }

    @Override
    protected Date getNextFireTime(JobPlan jobPlan) {
        if (jobPlan.getOnceExeTime().getTime() - System.currentTimeMillis() > WIN_TIME) {
            return jobPlan.getOnceExeTime();
        }

        return new Date(System.currentTimeMillis() + WIN_TIME);

    }

    @Override
    protected boolean isEnd(JobPlan jobPlan) {
        return Objects.nonNull(jobPlan.getLastTime()) || (Objects.nonNull(jobPlan.getEndTime())
                && jobPlan.getEndTime().compareTo(jobPlan.getOnceExeTime()) > 0
                && System.currentTimeMillis() - jobPlan.getEndTime().getTime() > WIN_TIME);
    }

}
