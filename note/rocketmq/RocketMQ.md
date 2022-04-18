## 安装
1. 设置 ROCKETMQ_HOME=D:\Soft\RocketMQ-4.9.0
2. CMD命令框执行进入bin目录下执行 start mqnamesrv.cmd 和 start mqbroker.cmd -n 127.0.0.1:9876
3. [https://github.com/apache/rocketmq-externals/tree/master/rocketmq-console](控制台插件)
#### 配置
```
namesrvAddr=127.0.0.1:9876
brokerIP1=127.0.0.1
autoCreateTopicEnable=true
enablePropertyFilter=true
```
 - start mqbroker.cmd -c ../conf/broker.conf
 - 只有Push类型的Consumer支持使用自定义属性过滤
#### 使用
- Group作用  

Producer Group作用如下：标识一类Producer 可以通过运维工具查询这个发送消息应用下有多个Producer实例,发送分布式事务消息时，如果Producer中途意外宕机，Broker会主动回调Producer Group内的任意一台机器来确认 事务状态。

Rocketmq集群有两种消费模式：默认是 CLUSTERING 模式，也就是同一个 Consumer group 里的多个消费者每人消费一部分，各自收到的消息内容不一样。 这种情况下，由 Broker 端存储和控制 Offset 的值，使用 RemoteBrokerOffsetStore 结构。
BROADCASTING模式下，每个 Consumer 都收到这个 Topic 的全部消息，各个 Consumer 间相互没有干扰， RocketMQ 使用 LocalfileOffsetStore，把 Offset存到本地。

- 保证顺序  

Rocketmq能够保证消息严格顺序，但是Rocketmq需要producer保证顺序消息按顺序发送到同一个queue中，比如购买流程(1)下单(2)支付(3)支付成功，这三个消息需要根据特定规则将这个三个消息按顺序发送到一个queue Producer端确保消息顺序唯一要做的事情就是将消息路由到特定的分区（这里的分区可以理解为不同的队列），在RocketMQ中，通过MessageQueueSelector来实现分区的选择。

RocketMQ是支持顺序消费的。但这个顺序，不是全局顺序，只是分区顺序。要全局顺序只能一个分区。一个queue只会有一个消费者，再加上MessageListenerOrderly即可保证。