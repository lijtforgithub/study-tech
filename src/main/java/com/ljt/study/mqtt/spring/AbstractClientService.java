package com.ljt.study.mqtt.spring;

import com.ljt.study.mqtt.MqttVersion;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author LiJingTang
 * @date 2022-06-30 15:33
 */
@Slf4j
public abstract class AbstractClientService {

    protected static final String LOCK_MQTT_PREFIX = "study:tech:mqtt:connect:";
    protected static final String MQTT_CLIENT_PREFIX = "study:tech:mqtt:client:";
    private static final Duration EXPIRE = Duration.ofSeconds(9);

    protected ScheduledExecutorService scheduledExecutorService;

    @Autowired
    protected ApplicationContext applicationContext;
    @Autowired
    protected StringRedisTemplate stringRedisTemplate;
    @Autowired
    protected RedisLockRegistry redisLockRegistry;

    protected final void renew() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (isConnected()) {
                stringRedisTemplate.expire(getClientKey(MQTT_CLIENT_PREFIX), EXPIRE);
            }
        }, 1, 6, TimeUnit.SECONDS);
    }

    @Scheduled(fixedDelayString = "10000")
    protected void connectServer() {
        if (isConnected()) {
            return;
        }

        Lock lock = redisLockRegistry.obtain(getClientKey(LOCK_MQTT_PREFIX));
        log.info("创建一个lock");
        if (lock.tryLock()) {
            log.info("获取锁成功");

            try {
                if (StringUtils.isBlank(stringRedisTemplate.opsForValue().get(getClientKey(MQTT_CLIENT_PREFIX)))) {
                    connect();

                    Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(getClientKey(MQTT_CLIENT_PREFIX), getClientId(), EXPIRE);
                    if (Boolean.FALSE.equals(success)) {
                        disconnect();
                    }
                }
            } catch (Exception e) {
                log.info("mqtt连接失败 {}", e.getMessage());
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }

    protected String getClientKey(String prefix) {
        return prefix + getClientId();
    }

    protected boolean isSubscribe() {
        return Arrays.stream(applicationContext.getEnvironment().getActiveProfiles()).noneMatch(p -> p.equals(MqttVersion.ONLY_SEND));
    }

    protected void disconnected() {
        log.error("连接断开");

        stringRedisTemplate.delete(getClientKey(MQTT_CLIENT_PREFIX));
        log.info("删除redis key [{}]", getClientKey(MQTT_CLIENT_PREFIX));
        if (Objects.nonNull(scheduledExecutorService)) {
            scheduledExecutorService.shutdown();
            log.info("关闭续约线程池 {}", scheduledExecutorService);
        }
    }


    protected abstract String getClientId();

    protected abstract void connect() throws Exception;

    protected abstract void disconnect() throws Exception;

    protected abstract boolean isConnected();

}
