package com.ljt.study.dingtalk.service;

import com.ljt.study.dingtalk.exception.DingTalkException;
import com.ljt.study.dingtalk.msg.DingTalkMessage;

import java.util.List;

/**
 * @author LiJingTang
 * @date 2022-07-22 9:19
 */
public interface DingTalkService {

    /**
     * 发送通知
     *
     * @param mobileList 手机号
     * @param message 通知内容
     *
     * @return 是否成功
     */
    boolean sendNotice(List<String> mobileList, DingTalkMessage message) throws DingTalkException;

    /**
     * 创建群会话
     *
     * @param mobileList 群成员手机号码
     * @param ownerMobile 群主 不指定默认取手机号集合第一个
     * @param name 群名称
     *
     * @return 群会话ID
     */
    String createChat(List<String> mobileList, String ownerMobile, String name) throws DingTalkException;

    /**
     * 添加群成员
     *
     * @param chatId 群会话ID
     * @param mobileList 手机号
     */
    void addCharUser(String chatId, List<String> mobileList) throws DingTalkException;

    /**
     * 发送群消息
     *
     * @param chatId 会话ID
     * @param message 消息
     * @return 消息ID
     */
    String sendChatMsg(String chatId, DingTalkMessage message) throws DingTalkException;

    /**
     * 启动时同步手机号-userId对应关系
     */
    void refreshUser();

}
