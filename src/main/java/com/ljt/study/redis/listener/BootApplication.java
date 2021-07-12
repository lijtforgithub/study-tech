package com.ljt.study.redis.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * @author LiJingTang
 * @date 2021-07-12 09:43
 */
@Slf4j
@SpringBootApplication
class BootApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }


    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        return container;
    }

    @Bean
    KeyExpirationEventMessageListener listener(RedisMessageListenerContainer container) {
        return new KeyExpirationEventMessageListener(container);
    }

    private static final String PREFIX = "test:key:expire:";

    @EventListener
    public void keyExpire(RedisKeyExpiredEvent<String> event) {
        final String key = new String(event.getSource());
        log.info("key过期：{}", key);
        if (key.startsWith(PREFIX)) {
            log.info("处理业务：{}", key.substring(PREFIX.length() - 1));
        }
    }

}
