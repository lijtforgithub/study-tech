package com.ljt.study.quartz.plan.service.impl;

import com.ljt.study.quartz.plan.dto.OptionDTO;
import com.ljt.study.quartz.plan.entity.JobPlan;
import com.ljt.study.quartz.plan.enums.PlanEnum;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.ScheduleBuilder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LiJingTang
 * @date 2023-03-08 13:48
 */
@Slf4j
@Service
public class MonthPlanServiceImpl extends AbstractPlanService {

    private static final List<OptionDTO> OPTION = new ArrayList() {
        {
            for (int i = 1; i <= 31; i++) {
                add(new OptionDTO(i, i + "号"));
            }
            add(new OptionDTO(-1, "最后一天"));
        }
    };

    @Override
    public PlanEnum getType() {
        return PlanEnum.MONTH;
    }

    @Override
    public List<OptionDTO> getOption() {
        return OPTION;
    }

    @Override
    protected ScheduleBuilder<?> buildSchedule(JobPlan jobPlan) {
        LocalTime cycleExeTime = jobPlan.getCycleExeTime();
        String option = jobPlan.getPlanOption() > 0 ? String.valueOf(jobPlan.getPlanOption()) : "L";
        String cronExpression = String.format("%d %d %d %s * ?", cycleExeTime.getSecond(), cycleExeTime.getMinute(), cycleExeTime.getHour(), option);

        try {
            return CronScheduleBuilder.cronSchedule(new CronExpression(cronExpression));
        } catch (ParseException e) {
            log.error("Cron表达式错误", e);
            return null;
        }
    }

}
