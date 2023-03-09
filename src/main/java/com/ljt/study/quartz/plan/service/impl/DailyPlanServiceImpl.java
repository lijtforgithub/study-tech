package com.ljt.study.quartz.plan.service.impl;

import com.ljt.study.quartz.plan.entity.JobPlan;
import com.ljt.study.quartz.plan.enums.PlanEnum;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.ScheduleBuilder;
import org.quartz.TimeOfDay;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

/**
 * @author LiJingTang
 * @date 2023-03-08 11:13
 */
@Service
public class DailyPlanServiceImpl extends AbstractPlanService {

    @Override
    public PlanEnum getType() {
        return PlanEnum.DAILY;
    }

    @Override
    protected ScheduleBuilder<?> buildSchedule(JobPlan jobPlan) {
        LocalTime cycleExeTime = jobPlan.getCycleExeTime();

        return DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().withIntervalInHours(24)
                .startingDailyAt(new TimeOfDay(cycleExeTime.getHour(), cycleExeTime.getMinute(), cycleExeTime.getSecond()));
    }

}
