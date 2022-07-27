package com.ljt.study.dingtalk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LiJingTang
 * @date 2022-07-22 10:08
 */
@Getter
@AllArgsConstructor
public enum ApiEnum {

    ACCESS_TOKEN("/gettoken?appkey={appKey}&appsecret={appSecret}", "获取访问凭证accessToken"),
    USER_BY_MOBILE("/topapi/v2/user/getbymobile?access_token={accessToken}", "根据手机号查询用户"),
    DEPART_LIST("/topapi/v2/department/listsub?access_token={accessToken}", "获取部门列表"),
    DEPART_USER("/topapi/v2/user/list?access_token={accessToken}", "获取部门用户详情"),
    SEND_NOTICE("/topapi/message/corpconversation/asyncsend_v2?access_token={accessToken}", "发送工作通知"),
    CREATE_CHAT("/chat/create?access_token={accessToken}", "创建群会话"),
    UPDATE_CHAT("/chat/update?access_token={accessToken}", "修改群会话"),
    CHAT_SEND("/chat/send?access_token={accessToken}", "发送消息到企业群");

    private final String path;
    private final String desc;

}
