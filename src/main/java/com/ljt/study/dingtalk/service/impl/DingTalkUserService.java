package com.ljt.study.dingtalk.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSON;
import com.caes.tickettrack.dingtalk.dto.request.DepartUserRequestDTO;
import com.caes.tickettrack.dingtalk.dto.request.UserByMobileRequestDTO;
import com.caes.tickettrack.dingtalk.dto.response.DepartListResponseDTO;
import com.caes.tickettrack.dingtalk.dto.response.DepartUserResponseDTO;
import com.caes.tickettrack.dingtalk.dto.response.UserByMobileResponseDTO;
import com.caes.tickettrack.dingtalk.enums.ApiEnum;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2022-07-22 11:20
 */
@Slf4j
@Service
class DingTalkUserService {

    /**
     * 获取部门用户详情 每次最大只能查询100
     */
    private static final int PAGE_SIZE = 100;

    /**
     * 缓存AccessToken
     */
    private static final long MAX_SIZE = 5000L;
    private static final Cache<String, String> USER_CACHE = CacheBuilder.newBuilder().maximumSize(MAX_SIZE).build();

    @Autowired
    private DingTalkWebClient dingTalkWebClient;

    /**
     * 根据手机号查询钉钉ID
     *
     * @param accessToken accessToken
     * @param mobile 手机号
     *
     * @return userId
     */
    String getUserId(final String accessToken, final String mobile) {
        final String errorMsg = "根据手机号【" + mobile + "】获取userId失败";

        try {
            return USER_CACHE.get(CharSequenceUtil.trim(mobile), () -> {
                log.info(ApiEnum.USER_BY_MOBILE.getDesc() + mobile);

                final UserByMobileResponseDTO dto = dingTalkWebClient.getWebClient().post()
                        .uri(ApiEnum.USER_BY_MOBILE.getPath(), accessToken)
                        .bodyValue(new UserByMobileRequestDTO().setMobile(CharSequenceUtil.trim(mobile)))
                        .retrieve()
                        .bodyToMono(UserByMobileResponseDTO.class)
                        .doOnError(err -> log.error(ApiEnum.USER_BY_MOBILE.getDesc(), err))
                        .block();

                if (Objects.isNull(dto) || !dto.isSuccess()) {
                    log.warn(ApiEnum.USER_BY_MOBILE.getDesc() + " {}", JSON.toJSONString(dto));
                    return null;
                }

                return dto.getResult().getUserid();
            });
        } catch (CacheLoader.InvalidCacheLoadException e) {
            log.warn("查询用户ID：{}", e.getMessage());
        } catch (Exception e) {
            log.error(errorMsg, e);
        }

        return null;
    }

    /**
     * 刷新所有员工
     *
     * @param accessToken accessToken
     */
    void refreshUser(String accessToken) {
        dingTalkWebClient.getWebClient().post()
                .uri(ApiEnum.DEPART_LIST.getPath(), accessToken)
                .retrieve()
                .bodyToMono(DepartListResponseDTO.class)
                .doOnError(err -> log.error(ApiEnum.DEPART_LIST.getDesc(), err))
                .subscribe(dto -> {
                    if (dto.isSuccess()) {
                        dto.getResult().forEach(depart -> addUser(accessToken, depart.getDept_id(), 0));
                    } else {
                        log.error(dto.getErrmsg());
                    }
                });
    }

    /**
     * 添加当前部门用户到缓存
     */
    private void addUser(String accessToken, String departId, Integer cursor) {
        log.info("查询部门【{}】员工", departId);
        DepartUserRequestDTO requestDTO = new DepartUserRequestDTO()
                .setContain_access_limit(Boolean.TRUE)
                .setSize(PAGE_SIZE)
                .setDept_id(departId)
                .setCursor(cursor);

        dingTalkWebClient.getWebClient().post()
                .uri(ApiEnum.DEPART_USER.getPath(), accessToken)
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(DepartUserResponseDTO.class)
                .doOnError(err -> log.error(ApiEnum.DEPART_USER.getDesc(), err))
                .subscribe(dto -> {
                    if (dto.isSuccess()) {
                        if (Objects.nonNull(dto.getResult())) {
                            DepartUserResponseDTO.Result result = dto.getResult();
                            if (CollUtil.isNotEmpty(result.getList())) {
                                result.getList().forEach(user -> USER_CACHE.put(user.getMobile(), user.getUserid()));
                            }
                            if (Boolean.TRUE.equals(result.getHasMore())) {
                                addUser(accessToken, departId, result.getNext_cursor());
                            }
                        }
                    } else {
                        log.error(dto.getErrmsg());
                    }
                });

    }

}
