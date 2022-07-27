package com.ljt.study.dingtalk.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author LiJingTang
 * @date 2022-07-22 16:58
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ChatCreateResponseDTO extends BaseResponseDTO {

    private String chatid;

}
