package com.ljt.study.rocketmq.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jtli3
 * @date 2022-03-25 16:17
 */
@Data
@ConfigurationProperties(prefix = "rocketmq.custom")
public class RocketMQCustomProperties {

    private String repeatPrefix = "rocketmq:repeat";
    private Long repeatCacheTime = 3600L;

    /**
     * 消费失败 再次投递次数
     */
    private Integer maxReconsumeTimes = 2;
    /**
     * MessageListenerOrderly
     * 串行消费 如果失败 再次消费的时间间隔 ms
     */
    private Long suspendCurrentQueueTimeMillis = 2000L;
    /**
     * MessageListenerConcurrently
     * 并行消费下次投递间隔等级
     * RocketMQ默认等级的配置是messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     * 例如值为3 则间隔10s
     */
    private Integer delayLevelWhenNextConsume = 2;

}
