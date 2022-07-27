package com.ljt.study.dingtalk.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author LiJingTang
 * @date 2022-07-22 15:35
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class NoticeSendResponseDTO extends BaseResponseDTO {

    /**
     * 任务ID
     */
    private String task_id;

}
