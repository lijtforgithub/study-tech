package com.ljt.study.rocketmq.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author jtli3
 * @date 2022-03-25 13:36
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class RocketMQConfig {

    @Bean
    RocketMQCustomProperties rocketMQCustomProperties() {
        return new RocketMQCustomProperties();
    }

    @Bean
    RepeatConsumePostProcessor repeatConsumePostProcessor(StringRedisTemplate stringRedisTemplate, RocketMQCustomProperties customProperties) {
        log.debug("RepeatConsumePostProcessor 实例化: {}", customProperties.getRepeatPrefix());
        RepeatConsumePostProcessor postProcessor = new RepeatConsumePostProcessor();
        postProcessor.setPrefix(customProperties.getRepeatPrefix());
        postProcessor.setCacheTime(customProperties.getRepeatCacheTime());
        postProcessor.setStringRedisTemplate(stringRedisTemplate);
        return postProcessor;
    }

    @Bean
    @ConditionalOnMissingBean(MessageErrorHandler.class)
    MessageErrorHandler defaultErrorHandler() {
        log.debug("默认 MessageErrorHandler 实例化");

        return (message, e) -> log.error("处理消息[" + message.getMsgId() + "]异常");
    }

    @Bean
    RocketMQContainerPostProcessor rocketMQContainerProcessor(RocketMQCustomProperties rocketMQCustomProperties) {
        return new RocketMQContainerPostProcessor(rocketMQCustomProperties);
    }

}
