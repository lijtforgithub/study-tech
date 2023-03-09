package com.ljt.study.quartz.plan.service.impl;

import cn.hutool.core.convert.NumberChineseFormatter;
import com.ljt.study.quartz.plan.dto.OptionDTO;
import com.ljt.study.quartz.plan.entity.JobPlan;
import com.ljt.study.quartz.plan.enums.PlanEnum;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.ScheduleBuilder;
import org.quartz.TimeOfDay;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LiJingTang
 * @date 2023-03-08 11:43
 */
@Service
public class WeekPlanServiceImpl extends AbstractPlanService {

    private static final List<OptionDTO> OPTION = new ArrayList() {
        {
            for (int i = 1; i < 7; i++) {
                add(new OptionDTO(i, "周" +  NumberChineseFormatter.format(i, false)));
            }
            add(new OptionDTO(7, "周日"));
        }
    };

    @Override
    public PlanEnum getType() {
        return PlanEnum.WEEK;
    }

    @Override
    public List<OptionDTO> getOption() {
        return OPTION;
    }

    @Override
    protected ScheduleBuilder<?> buildSchedule(JobPlan jobPlan) {
        LocalTime cycleExeTime = jobPlan.getCycleExeTime();
        int daysOfWeek = (jobPlan.getPlanOption() % 7) + 1;

        return DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().withIntervalInHours(24).onDaysOfTheWeek(daysOfWeek)
                .startingDailyAt(new TimeOfDay(cycleExeTime.getHour(), cycleExeTime.getMinute(), cycleExeTime.getSecond()));
    }

}
