package com.ljt.study.rocketmq.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

    @ConditionalOnProperty(prefix = "rocketmq.custom", value = "repeat-enable", havingValue = "true")
    @Bean
    MessageProcessor repeatConsumeProcessor(StringRedisTemplate stringRedisTemplate, RocketMQCustomProperties customProperties) {
        log.debug("{} 实例化: {}", RepeatConsumeProcessor.class.getSimpleName(), customProperties.getRepeatPrefix());
        RepeatConsumeProcessor postProcessor = new RepeatConsumeProcessor();
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
