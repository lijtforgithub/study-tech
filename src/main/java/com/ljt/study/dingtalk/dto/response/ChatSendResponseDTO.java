package com.ljt.study.dingtalk.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author LiJingTang
 * @date 2022-07-25 10:11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ChatSendResponseDTO extends BaseResponseDTO {

    /**
     * 加密的消息ID
     */
    private String messageId;

}
