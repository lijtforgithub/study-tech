#### 安装管理界面插件
在rabbitmq的安装目录下找到sbin目录；打开命令行执行：rabbitmq-plugins enable rabbitmq_management

## 队列
创建一个已经存在的队列或Exchange，参数和原有的相同是没问题的，但是如果第二次创建的参数和已经存在的不一样，会抛异常。  
如果队列不存在，Consumer 会抛异常。但是Producer 发送的消息会被丢弃。所以为了数据不丢失，Consumer和Producer都应该去创建队列，这样不会出问题。
- durable = false;  持久化 服务器重启后队列还在
- exclusive = true;  独占队列 仅限于此连接 连接关闭自动删除（有消息也会）
- autoDelete = true;  自动删除队列 服务器不再使用是自动删除（消息消费完）

1. channel.waitForConfirms() 普通发送方确认模式
2. channel.waitForConfirmsOrDie() 批量确认模式
3. channel.addConfirmListener() 异步监听发送方确认模式

## 路由
Producer 只能发送消息到Exchange，它是不能直接发送到Queue的。如果Exchange不存在，会抛异常  
Producer 发送的消息进入了Exchange。接着通过 RouteKey，RabbitMQ会找到应该把这个消息放到哪个队列里。队列也是通过这个RouteKey来绑定路由。
#### Direct Exchange
The default exchange is implicitly bound to every queue, with a routing key equal to the queue name.
It it not possible to explicitly bind to, or unbind from the default exchange. It also cannot be deleted.

默认的Direct Exchange（名字是空字符）。这个默认的Exchange允许我们发送给指定的队列。RouteKey就是指定的队列名字。
发送到Direct Exchange的消息都会被转发到RouteKey中指定的Queue上。如果不存在RouteKey的绑定，则该消息会被抛弃。
#### Fanout Exchange
任何发送到Fanout Exchange的消息都会被转发到与该Exchange绑定的所有队列上。这种绑定不需要RouteKey。  
这种模式需要提前将Exchange与Queue进行绑定，一个Exchange可以绑定多个队列，一个队列可以同多个Exchange进行绑定。如果接受到消息的Exchange没有与任何Queue绑定，则消息会被抛弃。
#### Topic Exchange
任何发送到Topic Exchange的消息都会被转发到所有关心RouteKey中指定话题的Queue上。这种模式需要RouteKey提前绑定Exchange与队列。  
\* (星号) 代表任意 一个单词；# (hash) 0个或者多个单词。如果Exchange没有发现能够与RouteKey匹配的队列，则会抛弃此消息。

> 性能排序：fanout > direct > topic 比例大约为11：10：6

## 消费者
1. 推模式：消息中间件主动将消息推送给消费者 channel.basicConsume
2. 拉模式：消费者主动从消息中间件拉取消息 channel.basicGet

对于多个Consumer来说，RabbitMQ 使用循环的方式（round-robin）的方式均衡的发送给不同的Consumer。  
默认状态下，RabbitMQ将第n个Message分发给第n个Consumer。当然n是取余后的。它不管Consumer是否还有unacked Message，只是按照这个默认机制进行分发。  
channel.basicQos(1);这样RabbitMQ就会使得每个Consumer在同一个时间点最多处理一个Message。换句话说，在接收到该Consumer的ack前，他它不会将新的Message分发给它。  
这种方法可能会导致queue满。这种情况下你可能需要添加更多的Consumer，或者创建更多的virtualHost来细化。

> basicAck 方法的第二个参数 multiple 取值为 false 时，表示通知 RabbitMQ 当前消息被确认；如果为 true，则额外将比第一个参数指定的 deliveryTag 小的消息一并确认。
