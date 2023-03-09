package com.ljt.study.quartz.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author LiJingTang
 * @date 2023-03-08 17:09
 */
@Data
public class NextFireDTO {

    private boolean end;
    private Date nextFireTime;

}
