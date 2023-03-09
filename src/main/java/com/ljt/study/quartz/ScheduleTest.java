package com.ljt.study.quartz;

import cn.hutool.core.date.DateUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.quartz.*;

import java.util.Date;
import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2023-03-07 15:36
 */
@Slf4j
class ScheduleTest {

    private static final Date START_DATE = DateUtil.parseDateTime("2023-03-09 10:10:10");

    @SneakyThrows
    @Test
    void testDaily() {
        DailyTimeIntervalScheduleBuilder builder = DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().withIntervalInHours(1)
                .startingDailyAt(new TimeOfDay(8, 9));
        DailyTimeIntervalTrigger trigger = TriggerBuilder.newTrigger().withSchedule(builder)
                .startAt(START_DATE).build();

        print(trigger.getFireTimeAfter(new Date()));
    }

    @SneakyThrows
    @Test
    void testCalendar() {
        CalendarIntervalScheduleBuilder builder = CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withIntervalInWeeks(1);
        CalendarIntervalTrigger trigger = TriggerBuilder.newTrigger().withSchedule(builder).startAt(START_DATE).build();

        print(trigger.getFireTimeAfter(new Date()));
    }

    @SneakyThrows
    @Test
    void testCron() {
        CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule("1 1 1 30 * ?");
        Trigger trigger = TriggerBuilder.newTrigger().withSchedule(builder).build();

        print(trigger.getFireTimeAfter(null));
    }

    private void print(Date date) {
        log.info("执行时间：{}", Objects.nonNull(date) ? DateUtil.formatDateTime(date) : null);
    }

}
