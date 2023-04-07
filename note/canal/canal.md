#### 准备
对于自建 MySQL , 需要先开启 Binlog 写入功能，配置 binlog-format 为 ROW 模式，my.cnf 中配置如下

```shell
[mysqld]
log-bin=mysql-bin # 开启 binlog
binlog-format=ROW # 选择 ROW 模式
server_id=1 # 配置 MySQL replaction 需要定义，不要和 canal 的 slaveId 重复
```

SHOW VARIABLES LIKE 'log_bin';

SHOW VARIABLES LIKE 'binlog_format';

授权 canal 链接 MySQL 账号具有作为 MySQL slave 的权限, 如果已有账户可直接 grant

```shell
CREATE USER canal IDENTIFIED BY 'canal';  
GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT, SHOW VIEW ON *.* TO 'canal'@'%';
-- GRANT ALL PRIVILEGES ON *.* TO 'canal'@'%' ;
FLUSH PRIVILEGES;
```

show grants for 'canal';  
show master status ;  
show slave status ;  

#### 配置

- canal.serverMode 启动后在admin修改不可以
- canal.serverMode = rabbitMQ（配置以服务的canal.properties有效）

- canal.instance.master.timestamp
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