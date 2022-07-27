package com.ljt.study.dingtalk.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSON;
import com.caes.tickettrack.dingtalk.dto.request.ChatCreateRequestDTO;
import com.caes.tickettrack.dingtalk.dto.request.ChatSendRequestDTO;
import com.caes.tickettrack.dingtalk.dto.request.ChatUpdateRequestDTO;
import com.caes.tickettrack.dingtalk.dto.request.NoticeSendRequestDTO;
import com.caes.tickettrack.dingtalk.dto.response.BaseResponseDTO;
import com.caes.tickettrack.dingtalk.dto.response.ChatCreateResponseDTO;
import com.caes.tickettrack.dingtalk.dto.response.ChatSendResponseDTO;
import com.caes.tickettrack.dingtalk.dto.response.NoticeSendResponseDTO;
import com.caes.tickettrack.dingtalk.enums.ApiEnum;
import com.caes.tickettrack.dingtalk.exception.DingTalkException;
import com.caes.tickettrack.dingtalk.msg.DingTalkMessage;
import com.caes.tickettrack.dingtalk.properties.DingTalkProperties;
import com.caes.tickettrack.dingtalk.service.DingTalkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author LiJingTang
 * @date 2022-07-22 14:57
 */
@Slf4j
@Service
public class DingTalkServiceImpl implements DingTalkService {

    /**
     * 发送通知 每次最多只能发送100人
     */
    private static final int NOTICE_SIZE = 100;
    /**
     * 每次最多支持40人
     */
    private static final int CHAT_SIZE = 40;

    @Autowired
    private DingTalkProperties dingTalkProperties;
    @Autowired
    private DingTalkWebClient dingTalkWebClient;
    @Autowired
    private DingTalkUserService dingTalkUserService;
    @Autowired
    private AccessTokenService accessTokenService;

    @Override
    public boolean sendNotice(List<String> mobileList, DingTalkMessage message) {
        Assert.notNull(message, () -> new DingTalkException("消息内容为空"));
        Assert.notEmpty(mobileList, () -> new DingTalkException("手机号为空"));

        String accessToken = accessTokenService.getAccessToken();
        if (CharSequenceUtil.isBlank(accessToken)) {
            log.warn("accessToken为空");
            return false;
        }

        final NoticeSendRequestDTO requestDTO = new NoticeSendRequestDTO()
                .setAgent_id(dingTalkProperties.getAgentId())
                .setMsg(message);
        List<String> userIds = getUserIds(mobileList, accessToken);

        int count = userIds.size() / NOTICE_SIZE + 1;
        for (int i = 0; i < count; i++) {
            List<String> list = userIds.subList(NOTICE_SIZE * i, Math.min(userIds.size(), NOTICE_SIZE * (i + 1)));
            requestDTO.setUserid_list(String.join(",", list));

            final NoticeSendResponseDTO dto = dingTalkWebClient.getWebClient().post()
                    .uri(ApiEnum.SEND_NOTICE.getPath(), accessToken)
                    .bodyValue(requestDTO)
                    .retrieve()
                    .bodyToMono(NoticeSendResponseDTO.class)
                    .doOnError(err -> log.error(ApiEnum.DEPART_USER.getDesc(), err))
                    .block();

            if (Objects.isNull(dto) || !dto.isSuccess()) {
                log.error("{} count={} i={} {}", ApiEnum.SEND_NOTICE.getDesc(), count, i, JSON.toJSONString(dto));
                return false;
            }
        }

        return true;
    }

    private List<String> getUserIds(List<String> mobileList, String accessToken) {
        return mobileList.stream().filter(CharSequenceUtil::isNotBlank)
                .map(mobile -> dingTalkUserService.getUserId(accessToken, mobile))
                .filter(CharSequenceUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public String createChat(List<String> mobileList, String ownerMobile, String name) throws DingTalkException {
        Assert.notBlank(name, () -> new DingTalkException("群名称为空"));
        Assert.notEmpty(mobileList, () -> new DingTalkException("群成员手机号为空"));
        String accessToken = accessTokenService.getAccessToken();
        Assert.notBlank(accessToken, () -> new DingTalkException("accessToken为空"));

        if (CharSequenceUtil.isBlank(ownerMobile)) {
            ownerMobile = mobileList.get(0);
        }
        mobileList.add(ownerMobile);

        final String owner = dingTalkUserService.getUserId(accessToken, ownerMobile);
        final List<String> userIds = getUserIds(mobileList, accessToken);
        Assert.notEmpty(userIds, () -> new DingTalkException("有效用户ID为空"));
        Assert.isTrue(userIds.size() <= 1000, () -> new DingTalkException("群成员人数上限为1000"));

        final ChatCreateRequestDTO requestDTO = new ChatCreateRequestDTO()
                .setName(name)
                .setOwner(owner);

        final int count = userIds.size() / CHAT_SIZE + 1;
        List<String> list = userIds.subList(0, Math.min(userIds.size(), CHAT_SIZE));
        requestDTO.setUseridlist(list);

        final ChatCreateResponseDTO dto = dingTalkWebClient.getWebClient().post()
                .uri(ApiEnum.CREATE_CHAT.getPath(), accessToken)
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(ChatCreateResponseDTO.class)
                .doOnError(err -> log.error(ApiEnum.CREATE_CHAT.getDesc(), err))
                .block();

        if (Objects.isNull(dto) || !dto.isSuccess()) {
            log.error("{} {}", ApiEnum.CREATE_CHAT.getDesc(), JSON.toJSONString(dto));
            throw new DingTalkException("创建群会话失败" + (Objects.nonNull(dto) ? dto.getErrmsg() : ""));
        }

        if (count > 1) {
            for (int i = 1; i < count; i++) {
                list = userIds.subList(CHAT_SIZE * i, Math.min(userIds.size(), CHAT_SIZE * (i + 1)));
                addCharUser(accessToken, dto.getChatid(), list, false);
            }
        }

        return dto.getChatid();
    }

    @Override
    public void addCharUser(String chatId, List<String> mobileList) throws DingTalkException {
        Assert.notBlank(chatId, () -> new DingTalkException("群会话ID为空"));
        Assert.notEmpty(mobileList, () -> new DingTalkException("群成员手机号为空"));
        String accessToken = accessTokenService.getAccessToken();
        Assert.notBlank(accessToken, () -> new DingTalkException("accessToken为空"));

        final List<String> userIds = getUserIds(mobileList, accessToken);
        final int count = userIds.size() / CHAT_SIZE + 1;

        for (int i = 0; i < count; i++) {
            List<String> list = userIds.subList(CHAT_SIZE * i, Math.min(userIds.size(), CHAT_SIZE * (i + 1)));
            addCharUser(accessToken, chatId, list, true);
        }
    }

    /**
     * 添加群成员
     */
    private void addCharUser(String accessToken, String chatId, List<String> userIds, boolean isBlock) {
        final ChatUpdateRequestDTO requestDTO = new ChatUpdateRequestDTO()
                .setChatid(chatId)
                .setAdd_useridlist(userIds);

        Mono<BaseResponseDTO> mono = dingTalkWebClient.getWebClient().post()
                .uri(ApiEnum.UPDATE_CHAT.getPath(), accessToken)
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(BaseResponseDTO.class)
                .doOnError(err -> log.error(ApiEnum.UPDATE_CHAT.getDesc(), err));

        if (isBlock) {
            BaseResponseDTO dto = mono.block();
            if (Objects.isNull(dto) || !dto.isSuccess()) {
                log.error("{} {}", ApiEnum.UPDATE_CHAT.getDesc(), JSON.toJSONString(dto));
                throw new DingTalkException("添加群成员失败" + (Objects.nonNull(dto) ? dto.getErrmsg() : ""));
            }

        } else {
            mono.subscribe(dto -> {
                if (!dto.isSuccess()) {
                    log.error("添加群【{}】成员失败 {}", chatId, dto.getErrmsg());
                }
            });
        }
    }

    @Override
    public String sendChatMsg(String chatId, DingTalkMessage message) {
        Assert.notBlank(chatId, () -> new DingTalkException("会话ID为空"));
        Assert.notNull(message, () -> new DingTalkException("消息内容为空"));
        String accessToken = accessTokenService.getAccessToken();
        Assert.notBlank(accessToken, () -> new DingTalkException("accessToken为空"));

        final ChatSendRequestDTO requestDTO = new ChatSendRequestDTO().setChatid(chatId).setMsg(message);

        final ChatSendResponseDTO dto = dingTalkWebClient.getWebClient().post()
                .uri(ApiEnum.CHAT_SEND.getPath(), accessToken)
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(ChatSendResponseDTO.class)
                .doOnError(err -> log.error(ApiEnum.CHAT_SEND.getDesc(), err))
                .block();

        if (Objects.isNull(dto) || !dto.isSuccess()) {
            log.error("{} {}", ApiEnum.CHAT_SEND.getDesc(), JSON.toJSONString(dto));
            throw new DingTalkException("发送群消息失败" + (Objects.nonNull(dto) ? dto.getErrmsg() : ""));
        }

        return dto.getMessageId();
    }

    @Override
    public void refreshUser() {
        String accessToken = accessTokenService.getAccessToken();
        if (CharSequenceUtil.isNotBlank(accessToken)) {
            dingTalkUserService.refreshUser(accessToken);
        }
    }

}
