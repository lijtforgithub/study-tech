- canal.serverMode 启动后在admin修改不可以
- canal.serverMode = rabbitMQ（配置以服务的canal.properties有效）
```
##################################################
######### 		    RabbitMQ	     #############
##################################################
rabbitmq.host = 127.0.0.1
rabbitmq.virtual.host = /
rabbitmq.exchange = canal.exchange
rabbitmq.username = guest
rabbitmq.password = guest
rabbitmq.deliveryMode = 2

# mq config
canal.mq.topic=canal.queue.kangjian
```