package com.ljt.study.rocketmq.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Objects;

/**
 * @author jtli3
 * @date 2022-03-28 13:45
 */
@Slf4j
class RocketMQContainerPostProcessor implements BeanPostProcessor {

    private final RocketMQCustomProperties customProperties;

    public RocketMQContainerPostProcessor(RocketMQCustomProperties rocketMQCustomProperties) {
        this.customProperties = rocketMQCustomProperties;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DefaultRocketMQListenerContainer) {
            DefaultRocketMQListenerContainer container = (DefaultRocketMQListenerContainer) bean;
            RocketMQListener<?> mqListener = container.getRocketMQListener();

            if (mqListener instanceof AbstractRocketMQListener) {
                AbstractRocketMQListener<?> listener = (AbstractRocketMQListener<?>) mqListener;
                container.setSuspendCurrentQueueTimeMillis(listener.getSuspendCurrentQueueTimeMillis());
                container.setDelayLevelWhenNextConsume(listener.getDelayLevelWhenNextConsume());
                checkAndSet(container.getConsumer(), listener.getMaxReconsumeTimes());
            } else {
                container.setSuspendCurrentQueueTimeMillis(customProperties.getSuspendCurrentQueueTimeMillis());
                container.setDelayLevelWhenNextConsume(customProperties.getDelayLevelWhenNextConsume());
                checkAndSet(container.getConsumer(), customProperties.getMaxReconsumeTimes());
            }
        }

        return bean;
    }

    private void checkAndSet(DefaultMQPushConsumer consumer, int maxReconsumeTimes) {
        if (Objects.isNull(consumer) || consumer.getMaxReconsumeTimes() != -1) {
            return;
        }
        consumer.setMaxReconsumeTimes(maxReconsumeTimes);
    }

}
