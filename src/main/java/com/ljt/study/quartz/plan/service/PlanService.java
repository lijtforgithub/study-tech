package com.ljt.study.quartz.plan.service;

import com.ljt.study.quartz.plan.dto.NextFireDTO;
import com.ljt.study.quartz.plan.dto.OptionDTO;
import com.ljt.study.quartz.plan.entity.JobPlan;
import com.ljt.study.quartz.plan.enums.PlanEnum;

import java.util.List;

/**
 * @author LiJingTang
 * @date 2023-03-08 11:09
 */
public interface PlanService {

    PlanEnum getType();

    List<OptionDTO> getOption();

    boolean contains(int value);

    NextFireDTO getNext(JobPlan jobPlan);

}
