package com.ljt.study.dingtalk.dto.request;

import com.caes.tickettrack.dingtalk.msg.DingTalkMessage;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author LiJingTang
 * @date 2022-07-25 10:10
 */
@Data
@Accessors(chain = true)
public class ChatSendRequestDTO {

    /**
     * 群会话的ID
     */
    private String chatid;
    /**
     * 消息内容，最长不超过2048个字节
     */
    private DingTalkMessage msg;

}
