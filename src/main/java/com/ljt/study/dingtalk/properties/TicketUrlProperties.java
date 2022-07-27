package com.ljt.study.dingtalk.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author LiJingTang
 * @date 2022-07-25 11:11
 */
@Data
@Component
@ConfigurationProperties(prefix = "ticket.url")
public class TicketUrlProperties {

    /**
     * 工单详情
     */
    private String ticketDetail;
    /**
     * 待处理列表
     */
    private String todoList;
    /**
     * 工单池列表
     */
    private String poolList;
    /**
     * 处理中列表
     */
    private String handleList;

}
