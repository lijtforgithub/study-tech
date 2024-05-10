#### 准备
https://github.com/alibaba/canal/wiki/%E7%AE%80%E4%BB%8B
https://github.com/alibaba/canal/wiki/AdminGuide

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
show master status;  
show slave status;

SHOW BINARY LOGS;  
PURGE BINARY LOGS TO 'mysql-bin.009000';

#### 配置

- canal.serverMode 启动后在admin修改不可以
- canal.serverMode = rabbitMQ（配置以server的canal.properties有效）

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

#### 测试总结
- h2.mv.db 是保存tsdb相关配置
- canal.instance.global.spring.xml = classpath:spring/file-instance.xml 位点数据保存在meta.dat
- 单机模式；多个client（微服务）连接同一个instance；每个client都可以建立连接并消费binlog
- 集群模式；多个client（微服务）连接同一个instance；只有一个client可以建立连接
- 如果canal.auto.scan=false 在admin界面上新增instance启动不成功；不会在server的conf下创建instance目录
- canal.instance.global.mode = spring  

  canal配置方式有两种： ManagerCanalInstanceGenerator： 基于manager管理的配置方式，目前alibaba内部配置使用这种方式。大家可以实现CanalConfigClient，连接各自的管理系统，即可完成接入。
  SpringCanalInstanceGenerator：基于本地spring xml的配置方式，目前开源版本已经自带该功能所有代码，建议使用