package com.ljt.study.dingtalk.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ljt.study.dingtalk.dto.response.AccessTokenResponseDTO;
import com.ljt.study.dingtalk.enums.ApiEnum;
import com.ljt.study.dingtalk.properties.DingTalkProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 获取Token
 *
 * @author LiJingTang
 * @date 2020-04-18 22:31
 */
@Slf4j
@Service
class AccessTokenService {

    /**
     * 正常情况下access_token有效期为7200秒，有效期内重复获取返回相同结果，并自动续期。
     */
    private static final long EXPIRE_TIME = 7100L;

    /**
     * 缓存AccessToken
     */
    private static final Cache<String, String> TOKEN_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(EXPIRE_TIME, TimeUnit.SECONDS)
            .maximumSize(10).build();


    @Autowired
    private DingTalkProperties dingTalkProperties;
    @Autowired
    private DingTalkWebClient dingTalkWebClient;

    /**
     * 获取Token
     *
     * @return accessToken
     */
    String getAccessToken() {
        try {
            return TOKEN_CACHE.get(dingTalkProperties.getAgentId(), () -> {
                log.info(ApiEnum.ACCESS_TOKEN.getDesc());

                final AccessTokenResponseDTO dto = dingTalkWebClient.getWebClient().get()
                        .uri(ApiEnum.ACCESS_TOKEN.getPath(), dingTalkProperties.getAppKey(), dingTalkProperties.getAppSecret())
                        .retrieve()
                        .bodyToMono(AccessTokenResponseDTO.class)
                        .doOnError(WebClientResponseException.class, err -> {
                            log.error(ApiEnum.ACCESS_TOKEN.getDesc() + err.getRawStatusCode(), err);
                            throw err;
                        })
                        .onErrorReturn(new AccessTokenResponseDTO())
                        .block();

                if (Objects.isNull(dto) || !dto.isSuccess()) {
                    log.warn(ApiEnum.ACCESS_TOKEN.getDesc() + " {}", JSON.toJSONString(dto));
                    return null;
                }

                log.info("accessToken={}", dto.getAccess_token());

                return dto.getAccess_token();
            });
        } catch (Exception e) {
            log.error(ApiEnum.ACCESS_TOKEN.getDesc(), e);
            return null;
        }
    }

}
