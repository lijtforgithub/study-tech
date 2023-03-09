package com.ljt.study.quartz;

import cn.hutool.core.date.DateUtil;
import com.ljt.study.quartz.plan.dto.NextFireDTO;
import com.ljt.study.quartz.plan.entity.JobPlan;
import com.ljt.study.quartz.plan.enums.PlanEnum;
import com.ljt.study.quartz.plan.service.PlanService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2023-03-08 14:14
 */
@Slf4j
@SpringBootTest
class BootTest {

    private static final Date START_DATE = DateUtil.parseDateTime("2023-03-12 10:10:10");
    private static final Date END_DATE = DateUtil.parseDateTime("2023-03-13 10:10:10");

    @Autowired
    private List<PlanService> planServiceList;

    @Test
    void testPlan() {
        planServiceList.forEach(planService -> planService.getOption().forEach(System.out::println));
    }

    private PlanService getService(PlanEnum planEnum) {
        for (PlanService planService : planServiceList) {
            if (planEnum == planService.getType()) {
                return planService;
            }
        }

        return null;
    }

    @Test
    void testDateTime() {
        PlanEnum planEnum = PlanEnum.DATE_TIME;
        JobPlan jobPlan = new JobPlan();
        jobPlan.setName(planEnum.getDesc());
        jobPlan.setPlanType(planEnum.getValue());
        jobPlan.setOnceExeTime(DateUtil.parseDateTime("2023-03-08 08:09:10").toJdkDate());

        PlanService service = getService(planEnum);

        NextFireDTO dto = service.getNext(jobPlan);
        print(dto);

        jobPlan.setLastTime(dto.getNextFireTime());
        print(service.getNext(jobPlan));
    }

    @Test
    void testDaily() {
        PlanEnum planEnum = PlanEnum.DAILY;
        JobPlan jobPlan = new JobPlan();
        jobPlan.setName(planEnum.getDesc());
        jobPlan.setPlanType(planEnum.getValue());
        jobPlan.setCycleExeTime(LocalTime.of(9, 30));
        jobPlan.setStartTime(START_DATE);
        jobPlan.setEndTime(END_DATE);

        PlanService service = getService(planEnum);

        NextFireDTO dto = service.getNext(jobPlan);
        print(dto);

        jobPlan.setLastTime(dto.getNextFireTime());
        print(service.getNext(jobPlan));
    }

    @Test
    void testWeek() {
        PlanEnum planEnum = PlanEnum.WEEK;
        JobPlan jobPlan = new JobPlan();
        jobPlan.setName(planEnum.getDesc());
        jobPlan.setPlanType(planEnum.getValue());
        jobPlan.setPlanOption(4);
        jobPlan.setCycleExeTime(LocalTime.of(9, 30));

        PlanService service = getService(planEnum);

        NextFireDTO dto = service.getNext(jobPlan);
        print(dto);

        jobPlan.setLastTime(dto.getNextFireTime());
        print(service.getNext(jobPlan));
    }

    @Test
    void testMonth() {
        PlanEnum planEnum = PlanEnum.MONTH;
        JobPlan jobPlan = new JobPlan();
        jobPlan.setName(planEnum.getDesc());
        jobPlan.setPlanType(planEnum.getValue());
        jobPlan.setPlanOption(-1);
        jobPlan.setCycleExeTime(LocalTime.of(9, 30));

        PlanService service = getService(planEnum);

        NextFireDTO dto = service.getNext(jobPlan);
        print(dto);

        jobPlan.setLastTime(dto.getNextFireTime());
        print(service.getNext(jobPlan));
    }

    private void print(NextFireDTO dto) {
        log.info("执行时间：{} => {}", dto.isEnd(), Objects.nonNull(dto.getNextFireTime()) ? DateUtil.formatDateTime(dto.getNextFireTime()) : null);
    }

}
