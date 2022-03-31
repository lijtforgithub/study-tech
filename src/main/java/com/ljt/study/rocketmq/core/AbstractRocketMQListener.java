package com.ljt.study.rocketmq.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.support.RocketMQMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.SmartMessageConverter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * @author jtli3
 * @date 2022-03-24 15:39
 */
@Slf4j
public abstract class AbstractRocketMQListener<T> implements RocketMQListener<MessageExt>, MessageListenerOrderly, MessageListenerConcurrently {

    private Type messageType;
    private MethodParameter methodParameter;
    private MessageContext messageContext;

    @Autowired
    private RocketMQCustomProperties customProperties;
    @Autowired
    private Environment environment;

    @Autowired
    private RocketMQMessageConverter messageConverter;
    @Autowired
    private MessageErrorHandler errorHandler;
    @Autowired(required = false)
    private MessageProcessor messageProcessor;

    protected AbstractRocketMQListener() {
    }

    protected AbstractRocketMQListener(String consumerGroup) {
        this.messageContext = new MessageContext();
        this.messageContext.setConsumerGroup(consumerGroup);
        this.messageContext.setMessageModel(MessageModel.CLUSTERING);
    }

    protected AbstractRocketMQListener(MessageContext messageContext) {
        this.messageContext = messageContext;
    }

    @Override
    public void onMessage(MessageExt message) {
        log.debug("收到消息：{}", message.getMsgId());

        if (!before(message)) {
            return;
        }

        try {
            handleMessage((T) doConvertMessage(message));
            after(message);
        } catch (Exception e) {
            if (message.getReconsumeTimes() < getMaxReconsumeTimes()) {
                throw new RuntimeException(e.getMessage());
            }

            error(message, e);
        }
    }

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        for (MessageExt message : msgs) {
            try {
                onMessage(message);
            } catch (Exception e) {
                log.warn("consume message failed. messageId:{}, topic:{}, reconsumeTimes:{}", message.getMsgId(), message.getTopic(), message.getReconsumeTimes(), e);
                context.setSuspendCurrentQueueTimeMillis(getSuspendCurrentQueueTimeMillis());
                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
            }
        }

        return ConsumeOrderlyStatus.SUCCESS;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for (MessageExt message : msgs) {
            try {
                onMessage(message);
            } catch (Exception e) {
                log.warn("consume message failed. messageId:{}, topic:{}, reconsumeTimes:{}", message.getMsgId(), message.getTopic(), message.getReconsumeTimes(), e);
                context.setDelayLevelWhenNextConsume(getDelayLevelWhenNextConsume());
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 实现处理消息的方法
     *
     * @param msg 消息内容
     */
    public abstract void handleMessage(T msg);


    @PostConstruct
    public void init() {
        this.messageType = getMessageType();
        this.methodParameter = getMethodParameter();
        this.messageContext = getMessageContext();
    }

    private void error(MessageExt message, Exception e) {
        try {
            errorHandler.handleError(message, e);
        } catch (Exception e1) {
            log.error("error异常", e1);
        }
    }

    private boolean before(MessageExt message) {
        if (Objects.isNull(messageProcessor)) {
            return true;
        }

        try {
            return messageProcessor.beforeHandle(message, getMessageContext());
        } catch (Exception e) {
            log.error("before异常", e);
            return true;
        }
    }

    private void after(MessageExt message) {
        if (Objects.isNull(messageProcessor)) {
            return;
        }

        try {
            messageProcessor.beforeHandle(message, getMessageContext());
        } catch (Exception e) {
            log.error("after异常", e);
        }
    }

    protected MessageContext getMessageContext() {
        if (Objects.nonNull(messageContext)) {
            Assert.notNull(messageContext.getMessageModel(), "消费模式为空");
            return messageContext;
        }

        RocketMQMessageListener annotation = Objects.requireNonNull(getClass().getAnnotation(RocketMQMessageListener.class), getClass() + "未发现RocketMQMessageListener注解");
        String group = environment.resolveRequiredPlaceholders(annotation.consumerGroup());

        MessageContext context = new MessageContext();
        context.setConsumerGroup(group);
        context.setMessageModel(annotation.messageModel());
        return context;
    }

    private Type getMessageType() {
        if (Objects.nonNull(messageType)) {
            return messageType;
        }

        Class<?> clazz = getClass();
        Type matchedGenericType = null;

        while (Objects.nonNull(clazz)) {
            Type type = clazz.getGenericSuperclass();

            if (type instanceof ParameterizedType && (Objects.equals(((ParameterizedType) type).getRawType(), AbstractRocketMQListener.class))) {
                matchedGenericType = type;
                break;
            }

            clazz = clazz.getSuperclass();
        }
        if (Objects.isNull(matchedGenericType)) {
            return Object.class;
        }

        Type[] actualTypeArguments = ((ParameterizedType) matchedGenericType).getActualTypeArguments();
        if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0) {
            return actualTypeArguments[0];
        }

        return Object.class;
    }

    private MethodParameter getMethodParameter() {
        if (Objects.nonNull(methodParameter)) {
            return methodParameter;
        }

        Type type = getMessageType();
        Class<?> clazz;

        if (type instanceof ParameterizedType && getMessageConverter() instanceof SmartMessageConverter) {
            clazz = (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof Class) {
            clazz = (Class<?>) type;
        } else {
            throw new RuntimeException("parameterType:" + type + " of onMessage method is not supported");
        }

        try {
            final Method method = getClass().getMethod("handleMessage", clazz);
            return new MethodParameter(method, 0);
        } catch (NoSuchMethodException e) {
            log.error("获取泛型类型异常", e);
            throw new RuntimeException("parameterType:" + type + " of onMessage method is not supported");
        }
    }

    private Object doConvertMessage(MessageExt messageExt) {
        String str = new String(messageExt.getBody(), Charset.forName(StandardCharsets.UTF_8.name()));

        if (Objects.equals(messageType, String.class)) {
            return str;
        }

        // If msgType not string, use objectMapper change it.
        try {
            if (messageType instanceof Class) {
                //if the messageType has not Generic Parameter
                return getMessageConverter().fromMessage(MessageBuilder.withPayload(str).build(), (Class<?>) messageType);
            } else {
                //if the messageType has Generic Parameter, then use SmartMessageConverter#fromMessage with third parameter "conversionHint".
                //we have validate the MessageConverter is SmartMessageConverter in this#getMethodParameter.
                return ((SmartMessageConverter) getMessageConverter()).fromMessage(MessageBuilder.withPayload(str).build(), (Class<?>) ((ParameterizedType) messageType).getRawType(), methodParameter);
            }
        } catch (Exception e) {
            log.info("convert failed. str:{}, msgType:{}", str, messageType);
            throw new RuntimeException("cannot convert message to " + messageType, e);
        }
    }

    private MessageConverter getMessageConverter() {
        return messageConverter.getMessageConverter();
    }

    protected long getSuspendCurrentQueueTimeMillis() {
        return customProperties.getSuspendCurrentQueueTimeMillis();
    }

    protected int getDelayLevelWhenNextConsume() {
        return customProperties.getDelayLevelWhenNextConsume();
    }

    protected int getMaxReconsumeTimes() {
        return customProperties.getMaxReconsumeTimes();
    }

}
