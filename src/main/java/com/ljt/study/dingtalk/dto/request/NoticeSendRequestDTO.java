package com.ljt.study.dingtalk.dto.request;

import com.caes.tickettrack.dingtalk.msg.DingTalkMessage;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author LiJingTang
 * @date 2022-07-25 9:59
 */
@Data
@Accessors(chain = true)
public class NoticeSendRequestDTO {

    /**
     * 发送消息时使用的微应用的AgentID
     */
    private String agent_id;
    /**
     * 接收者的userid列表，最大用户列表长度100。
     */
    private String userid_list;
    /**
     * 消息内容，最长不超过2048个字节
     */
    private DingTalkMessage msg;

}
