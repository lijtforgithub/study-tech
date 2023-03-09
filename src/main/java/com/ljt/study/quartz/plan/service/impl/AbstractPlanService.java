package com.ljt.study.quartz.plan.service.impl;

import com.ljt.study.quartz.plan.dto.NextFireDTO;
import com.ljt.study.quartz.plan.dto.OptionDTO;
import com.ljt.study.quartz.plan.entity.JobPlan;
import com.ljt.study.quartz.plan.service.PlanService;
import org.quartz.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2023-03-08 11:59
 */
public abstract class AbstractPlanService implements PlanService {

    @Override
    public List<OptionDTO> getOption() {
        return Collections.emptyList();
    }

    @Override
    public boolean contains(int value) {
        List<OptionDTO> list = getOption();
        if (Objects.isNull(list) || list.isEmpty()) {
            return true;
        }

        for (OptionDTO o : list) {
            if (o.getValue() == value) {
                return true;
            }
        }
        return false;
    }

    @Override
    public NextFireDTO getNext(JobPlan jobPlan) {
        NextFireDTO dto = new NextFireDTO();
        dto.setEnd(isEnd(jobPlan));

        if (!dto.isEnd()) {
            dto.setNextFireTime(getNextFireTime(jobPlan));
        }
        return dto;
    }

    protected Date getNextFireTime(JobPlan jobPlan) {
        ScheduleBuilder<?> scheduleBuilder = buildSchedule(jobPlan);
        TriggerBuilder<?> triggerBuilder = TriggerBuilder.newTrigger().withSchedule(scheduleBuilder);

        Date date = new Date();
        if (Objects.nonNull(jobPlan.getStartTime()) && jobPlan.getStartTime().compareTo(date) > 0) {
            date = jobPlan.getStartTime();
        }
        // CalendarIntervalScheduleBuilder 的startAt的时分秒就是每周期执行的时间
        triggerBuilder.startAt(date);

        if (Objects.nonNull(jobPlan.getEndTime())) {
            triggerBuilder.endAt(jobPlan.getEndTime());
        }

        Trigger trigger = triggerBuilder.build();
        return trigger.getFireTimeAfter(jobPlan.getLastTime());
    }


    protected ScheduleBuilder<?> buildSchedule(JobPlan jobPlan) {
        return null;
    }

    protected boolean isEnd(JobPlan jobPlan) {
        return Objects.nonNull(jobPlan.getEndTime())
                && System.currentTimeMillis() > jobPlan.getEndTime().getTime();
    }


}
