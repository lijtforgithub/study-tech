## 安装

#### 步骤

```
1. yum install wget
2. cd /
   mkdir soft
   cd soft
3. wget http://download.redis.io/releases/redis-5.0.5.tar.gz
4. tar -xf redis-5.0.5.tar.gz
5. cd redis-5.0.5
6. README.md
7. make 报错后
   yum install gcc
   make distclean
8. cd src 生成了可执行程序
9. cd ..
10. make install PREFIX=/opt/redis5
11. vi /etc/profile 文件加入
    export REDIS_HOME=/opt/redis5
    export PATH=$PATH:$REDIS_HOME/bin
12. source /etc/profile
13. cd utils
14. ./install_server.sh 可执行一次或多次
    一个物理机中可以有多个redis实例，通过port区分。
    可执行程序就一份在目录，但是内存中存在未来的多个实例需要各自的配置文件，持久化目录等资源。
    service redis_6379 start/stop/status 服务目录 /etc/init.d/
15. ps -ef | grep redis

本机意外的ip能访问需要修改配置文件.conf
bind 0.0.0.0
protected-mode no
```

#### 启动&配置

```
启动服务：redis-server.exe redis.windows.conf
客户端：redis-cli.exe -h 127.0.0.1 -p 6379 [-a password] [--raw]
获取全部配置项：config get *
获取指定配置项：config get CONFIG_SETTING_NAME （config get port）
设置配置项：config set CONFIG_SETTING_NAME NEW_CONFIG_VALUE（config SET loglevel "notice"）
info memory
info persistence
info replication
```

```
auth password：验证密码是否正确
echo message：打印字符串
ping：查看服务是否运行
quit：关闭当前连接
select index：切换到指定的数据库

查看命令帮助文档
help @string
help @sorted-set
```

## 知识点

#### Redis 为什么这么快

1. 纯内存访问（内存的响应时间大约为100纳秒）
2. 工作线程单线程避免上下文切换（NIO/epoll）
   1. 之前单线程的原因：本身Redis的瓶颈不是CPU，而是内存和网络IO，QPS可以达到10W
   2. 针对一些超级大公司有更大QPS需求：6.0之后可以配置IO多线程（默认不开启），增加机器CPU（官方建议IO线程与CPU 核心数相匹配或稍小一些 <=4 过多的 IO 线程可能会导致上下文切换的开销增加，反而降低性能）配置可以提高更高的QPS
3. 扩缩容采用[渐进式Rehash](https://blog.csdn.net/weixin_42189550/article/details/127093319)
4. 缓存时间戳（每毫秒缓存一次 避免频繁系统调用）

#### 相比于Memcached

1. 值支持多种类型；每种类型提供方便高效的命令，计算向数据移动
2. memcached只有一种string类型；存JSON的话需要频繁的序列化和反序列化

#### AP模式

1. 怎么做CP

   全局配置 min-slaves-to-write
   默认异步 可以通过**`WAIT`** 命令阻塞当前客户端，直到指定数量的从节点确认接收写操作。

2. 二进制安全（hbase也是）

   存的是客户端发送的字节流；客户端根据编码规则解码（同一个中字；客户端编码不同；存进去之后strlen key不同）

   object encoding key (9999 占4个字节 4个assicll 码)

   ```shell
   # UTF-8
   127.0.0.1:6379> set k1 中
   OK
   127.0.0.1:6379> strlen k1
   3
   # GBK
   127.0.0.1:6379> get k1
   涓�
   127.0.0.1:6379> set k2 中
   OK
   127.0.0.1:6379> get k2
   中
   127.0.0.1:6379> strlen k2
   2
   127.0.0.1:6379> strlen k1
   3
   ```

#### 缓存是否适合

1. 业务数据常用吗？命中率如何？如果命中率很低，就没有必要写入缓存；
2. 该业务数据是读操作多，还是写操作多？如果写操作多，频繁需要写入数据库，也没有必要使用缓存；
3. 业务数据大小如何？如果要存储几百兆字节的文件，会给缓存带来很大的压力，这样也没有必要；

#### Redis常见性能问题和解决方案

1. Master最好不要做任何持久化工作，如RDB内存快照和AOF日志文件
2. 如果数据比较重要，某个Slave开启AOF备份数据，策略设置为每秒同步一次
3. 为了主从复制的速度和连接的稳定性，Master和Slave最好在同一个局域网内
4. 尽量避免在压力很大的主库上增加从库
5. 主从复制不要用图状结构，用单向链表结构更为稳定，即：Master <- Slave1 <- Slave2 <- Slave3

## 键和值

#### key

命令 | 描述
---|---
del KEY1 [KEY2 ...] | 该命令用于在 key 存在时删除 key
dump KEY | 序列化给定 key 并返回被序列化的值 
exists KEY | 检查给定 key 是否存在
expire KEY seconds | 为给定 key 设置过期时间以秒计
expireat KEY timestamp | expireat 的作用和 expire 类似，都用于为 key 设置过期时间。 不同在于 expireat 命令接受的时间参数是 UNIX 时间戳(unix timestamp)。
pexpire KEY milliseconds | 为设置 key 的过期以毫秒计
pexpireat KEY milliseconds-timestamp | 设置 key 过期时间的时间戳(unix timestamp) 以毫秒计
keys pattern | 查找所有符合给定模式( pattern)的 key
move key db | 将当前数据库的 key 移动到给定的数据库 db 当中
persists KEY | 移除 key 的过期时间，key 将持久保持
pttl KEY | 以毫秒为单位返回 key 的剩余的过期时间
ttl KEY | 以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)。
randomkey | 从当前数据库中随机返回一个 key
rename KEY NEW_KEY | 修改 key 的名称
renamenx KEY NEW_KEY | 仅当 newkey 不存在时，将 key 改名为 newkey
type KEY | 返回 key 所储存的值的类型
strlen KEY | 二进制安全 同一个中字；客户端编码不同 长度不同 
object encoding KEY | 执行过incr 命令类型之后 数值会变成int类型 防止下次在判断类型 
#### string
1. bitmap 用来做统计 占用空间少
1. 统计活跃用户
1. 下标和元素 0和-1

命令 | 描述
---|---
set KEY VALUE | 设置指定 key 的值
get KEY | 获取指定 key 的值
getrange KEY start end | 返回 key 中字符串值的子字符
getset KEY | 将给定 key 的值设为 value ，并返回 key 的旧值(old value)
mset KEY1 VALUE1 [KEY2 VALUE2 ...] | 同时设置一个或多个 key-value 对
msetnx KEY1 VALUE1 [KEY2 VALUE2 ...] | 同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在
mget KEY1 [KEY2 ...] | 获取所有(一个或多个)给定 key 的值
setbit KEY offset | 对 key 所储存的字符串值，设置或清除指定偏移量上的位(bit)
getbit KEY offset | 对 key 所储存的字符串值，获取指定偏移量上的位(bit)
setex KEY seconds VALUE | 将值 value 关联到 key ，并将 key 的过期时间设为 seconds (以秒为单位)
setnx KEY VALUE | 只有在 key 不存在时设置 key 的值
setrange KEY offset value | 用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始
strlen KEY | 返回 key 所储存的字符串值的长度
psetex KEY milliseconds VALUE | 这个命令和 SETEX 命令相似，但它以毫秒为单位设置 key 的生存时间，而不是像 setex 命令那样，以秒为单位
incr KEY | 将 key 中储存的数字值增一
incrby KEY increment | 将 key 所储存的值加上给定的增量值（increment）
incrbyfloat KEY increment | 将 key 所储存的值加上给定的浮点增量值（increment）
decr KEY | 将 key 中储存的数字值减一
decrby KEY decrement | key 所储存的值减去给定的减量值（decrement）
append KEY VALUE | 如果 key 已经存在并且是一个字符串， APPEND 命令将指定的 value 追加到该 key 原来值（value）的末尾
#### hash

1. 存储对象（不用存大JSON；计算向数据移动）

命令 | 描述
---|---
hset KEY field value | 将哈希表 key 中的字段 field 的值设为 value
hsetnx KEY field value| 只有在字段 field 不存在时，设置哈希表字段的值
hmset KEY field1 value1 [field2 value2 ...] | 同时将多个 field-value (域-值)对设置到哈希表 key 中
hget KEY field | 获取存储在哈希表中指定字段的值
hmget KEY field1 [field2 ...]| 获取所有给定字段的值
hlen KEY | 获取哈希表中字段的数量
hkeys KEY | 获取所有哈希表中的字段
hvals KEY | 获取哈希表中所有值
hgetall KEY | 获取在哈希表中指定 key 的所有字段和值
hexists KEY field | 查看哈希表 key 中，指定的字段是否存在
hdel KEY field1 [field2 ...] | 删除一个或多个哈希表字段
hincrby KEY field increment | 为哈希表 key 中的指定字段的整数值加上增量 increment
hincrbyfloat KEY field increment | 为哈希表 key 中的指定字段的浮点数值加上增量 increment
#### list
1. 栈 同向命令（lpush/lpop）
2. 队列 反向命令（lpush/rpop）
3. 数组 （lindex ）
4. 阻塞 单播队列 FIFO

命令 | 描述
---|---
lpush KEY value1 [value2 ...] | 将一个或多个值插入到列表头部
lpushx KEY value | 将一个值插入到已存在的列表头部
rpush KEY value1 [value2 ...] | 在列表中添加一个或多个值
rpushx KEY value | 为已存在的列表添加值
lset KEY index value | 通过索引设置列表元素的值
linsert KEY before\|after pivot value | 在列表的元素前或者后插入元素
llen KEY | 获取列表长度
lindex KEY index | 通过索引获取列表中的元素
lrange KEY start stop | 获取列表指定范围内的元素
lpop KEY | 移出并获取列表的第一个元素
rpop KEY | 移除列表的最后一个元素，返回值为移除的元素
lrem KEY count value | 移除列表元素。count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，count 。count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。count = 0 : 移除表中所有与 value 相等的值
ltrim key start stop | 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除
blpop KEY1 [KEY2 ...] timeout | 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
brpop KEY1 [KEY2 ...] timeout | 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
brpoplpush source destination timeout | 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
rpoplpush source destination | 移除列表的最后一个元素，并将该元素添加到另一个列表并返回
#### set
1. srandmember：count 为正数返回不重复、不会超过已有集合的数据；为负数返回会重复、满足指定数量的数据
1. srandmember  spop抽奖场景

命令 | 描述
---|---
sadd KEY value1 [value2 ...] | 向集合添加一个或多个成员
scard KEY | 获取集合的成员个数
smembers KEY | 返回集合中的所有成员
srandmember KEY [count] | 返回集合中一个或多个随机数
sismember KEY value | 判断 value 是否是集合的成员
spop KEY | 移除并返回集合中的一个随机元素
srem KEY value1 [value2 ...] | 移除集合中一个或多个成员
sdiff KEY1 [KEY2] | 返回给定所有集合的差集
sdiffstore destination KEY1 [KEY2] | 返回给定所有集合的差集并存储在 destination 中
sinter KEY1 [KEY2] | 返回给定所有集合的交集
sinter destination KEY1 [KEY2] | 返回给定所有集合的交集并存储在 destination 中
smove source destination value | 将 value 元素从 source 集合移动到 destination 集合
sunion KEY1 [KEY2] | 返回所有给定集合的并集
sunionstore destination KEY1 [KEY2]  | 所有给定集合的并集存储在 destination 集合中
sscan KEY cursor [match pattern] [count count] | 迭代集合中的元素，match 可使用正则
#### zset
1. 物理内存存储按照score左小右大
1. 跳表skipList数据结构（空间换时间）

命令 | 描述
---|---
zadd KEY score1 value1 [score2 value2] | 向有序集合添加一个或多个成员，或者更新已存在成员的分数
zcard KEY | 获取有序集合的成员数
zcount KEY min max | 计算在有序集合中指定区间分数的成员数
zincrby KEY increment value | 有序集合中对指定成员的分数加上增量 increment
zinterstore destination numkeys KEY1 [KEY2 ...] | 计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 key 中，numkeys和指定key一致
zlexcount KEY min max | 对于一个所有成员的分值都相同的有序集合键 key 来说， 这个命令会返回该集合中， 成员介于 min 和 max 范围内的元素数量。
zrange KEY start stop [withscores] | 通过索引区间返回有序集合成指定区间内的成员, 下标参数start 和 stop 都以 0为底，也就是说，以0表示有序集第一个成员，以 1表示有序集第二个成员，以此类推。你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推
zrangebylex KEY min max [limit offset count] | 通过字典区间返回有序集合的成员
zrangebyscore KEY min max [withscores] [limit offset count] | 通过分数返回有序集合指定区间内的成员
zrange KEY value | 返回有序集合中指定成员的索引
zrem KEY value1 [value2] | 移除有序集合中的一个或多个成员
zremrangebylex KEY min max | 移除有序集合中给定的字典区间的所有成员
zremrangebyrank KEY start stop | 移除有序集合中给定的排名区间的所有成员
zremrangebyscore KEY min max | 移除有序集合中给定的分数区间的所有成员
zrevrange KEY start stop [withscores] | 返回有序集中指定区间内的成员，通过索引，分数从高到底
zrevrangebyscore KEY min max [withscores] [limit offset count] | 返回有序集中指定分数区间内的成员，分数从高到低排序
zrevrank KEY value | 返回有序集合中指定成员的排名，有序集成员按分数值递减(从大到小)排序
zscore KEY value | 返回有序集中，成员的分数值
zunionstore destination numkeys KEY1 [KEY2 ...] | 计算给定的一个或多个有序集的并集，并存储在新的 key 中
zscan KEY cursor [match pattern] [count count] | 迭代有序集合中的元素（包括元素成员和元素分值）

#### HyperLogLog

1. 计算基数：计算网站的 UV（独立访客）

| 命令                             | 描述                                      |
| -------------------------------- | ----------------------------------------- |
| pfadd KEY [element [element ...] | 添加指定元素到 HyperLogLog 中             |
| pfcount KEY [KEY...]             | 返回给定 HyperLogLog 的基数估算值         |
| pfmerge D_KEY S_KEY[S_KEY...]    | 将多个 HyperLogLog 合并为一个 HyperLogLog |

#### Stream

1. 主要用于消息队列MQ；消息会持久化
2. \> 号表示从当前消费组的 last_delivered_id 后面开始读

| 命令                                                         | 描述                                         |
| ------------------------------------------------------------ | -------------------------------------------- |
| XREAD [COUNT count] [BLOCK milliseconds] STREAMS key [key ...] id [id ...] | xread count 2 streams mq1 0-0                |
| XGROUP [CREATE key groupname id-or-$] [SETID key groupname id-or-$] [DESTROY key groupname] [DELCONSUMER key groupname consumername] | xgroup create mq1 g0 0-0                     |
| XREADGROUP GROUP group consumer [COUNT count] [BLOCK milliseconds] [NOACK] STREAMS key [key ...] ID [ID ...] | xreadgroup group g0 c0 count 2 streams mq1 > |

#### GEO

1. 存储地理位置信息

#### 发布订阅

1. 消息不能持久化
2. 订阅之前发布的消息不会被消费

| 命令                                       | 描述                                   |
| ------------------------------------------ | -------------------------------------- |
| publish KEY message                        | 发布频道消息                           |
| subscribe KEY                              | 订阅频道                               |
| psubscribe pattern [pattern ...]           | 订阅一个或多个符合给定模式的频道       |
| pubsub subcommand [argument [argument ...] | 查看订阅与发布系统状态 pubsub channels |

#### 管道

1. 减少网络IO；提高QPS
2. 不保证顺序执行

#### 事务

1. 顺序执行
2. 批量操作在发送 EXEC 命令前被放入队列缓存；要么全部执行，要么全部不执行；语法检查通过之后全部都会执行
3. 不具有原子性；事务中任意命令执行失败，其余的命令依然被执行
4. 在事务执行过程，其他客户端提交的命令请求不会插入到事务执行命令序列中

#### LUA脚本

## 内存回收

### 删除策略

1. 惰性删除：当读/写一个已经过期的key时，会触发惰性删除策略，直接删除掉这个过期key。
2. 定期删除：由于惰性删除策略无法保证冷数据被及时删掉，Redis 周期性（默认每秒 10 次）随机扫描部分过期 Key，从过期字典中随机抽取 `20` 个 Key（可配置）。删除其中已过期的 Key。如果过期 Key 比例超过 `25%`，则重复执行。当前已用内存超过maxmemory限定时，触发内存回收策略。

### 内存回收策略

1. volatile-lru：从已设置过期时间的数据集（server.db[i].expires）中挑选最近最少使用的数据淘汰
2. volatile-ttl：从已设置过期时间的数据集（server.db[i].expires）中挑选将要过期的数据淘汰
3. volatile-random：从已设置过期时间的数据集（server.db[i].expires）中任意选择数据淘汰
4. allkeys-lru：从数据集（server.db[i].dict）中挑选最近最少使用的数据淘汰
5. allkeys-random：从数据集（server.db[i].dict）中任意选择数据淘汰
6. no-enviction（驱逐）：禁止驱逐数据

## 数据持久化

> 新版本都是混合模式

### RDB

将当前数据生成快照保存到硬盘，默认方式。RDB文件是经过压缩的二进制文件。压缩不是针对整个文件，而是对数据库中的字符串达到一定长度（20字节）时才会进行。

1. 手动触发

   1. save命令会阻塞Redis服务器进程，直到RDB文件创建完毕，基本被废弃。
   2. bgsave命令会创建一个子进程去创建RDB文件，主进程继续处理请求。只有fork子进程时会阻塞服务器。

2. 自动触发: save m n 当m秒发生n次变化时，会触发bgsave。900 1 | 300 10 | 60 10000

   1. serverCron是Redis服务器的周期性操作函数，默认每隔100ms执行一次；该函数对服务器的状态进行维护。
   2. dirty计数器是Redis服务器维护的一个状态，记录了上一次bgsave/save命令后，服务器进行了多少次增删改；而当bgsave/save执行完成后，归0。  
      set mykey hello +1次 | sadd myset v1 v2 v3 +3次
   3. lastsave时间戳也是Redis服务器维持的一个状态，记录的是上一次成功执行bgsave/save的时间。

   每隔100ms执行serverCron函数，遍历save m n配置的条件，只要有一个条件满足，就进行bgsave。对于每一个条件要满足 当前时间-lastsave > m 且 dirty >= n

> 主从复制场景，从节点执行全量复制操作，则主节点会执行bgsave，并将rdb文件发送给从节点。执行shutdown命令会自动执行RDB持久化。

- bgsave 执行流程

![](/Users/lijingtang/workspace/study/study-tech/note/redis/img/bgsave.png)

1. Redis父进程首先判断当前是否在执行save/bgsave/bgrewriteaof的子进程，如果在执行则bgsave命令直接返回。  
   bgsave/bgrewriteaof子进程不能同时执行，主要是性能方面考虑，两个并发的子进程同时执行大量的磁盘写操作，可能引起严重的性能问题。
2. 父进程执行fork操作创建子进程，这个过程中父进程是阻塞的，Redis不能执行来自客户端的任何命令。
3. 父进程fork后，bgsave命令返回Background saving started信息并不再阻塞父进程，并可以相应其他命令。
4. 子进程创建rdb文件，根据父进程内存快照生成临时快照文件，完成后对原有文件进行原子替换。
5. 子进程发送信号给父进程表示完成，父进程更新统计信息。

- 启动时加载
  RDB文件的载入工作是在服务器启动时自动执行的，没有专门的命令。 
  服务器载入期间处于阻塞状态，直到载入完成为止。如果文件损坏，日志中打印错误，Redis启动失败。

### AOF

将每次执行写命令保存到硬盘（类似MySQL的binlog）。实时性更好；主流的持久化方案。Redis服务器默认开启RDB，关闭AOF；打开配置 appendonly yes  
AOF记录每条写命令，因此不需要触发；执行流程如下

1. 命令追加：将Redis的写命令追加到缓冲区aof_buf。直接写入文件，硬盘IO是负载瓶颈。

2. 文件写入和文件同步：根据不同的同步策略将aof_buf中的内容同步到硬盘。appendfsync

   1. always：命令写入aof_buf后立即调用系统fsync操作同步到AOF文件，fsync完成后线程返回。每次写命令都要同步，硬盘IO是性能瓶颈。
   2. no: 命令写入aof_buf后调用系统write操作，不对AOF文件做fsync同步；同步由操作系统负责，通常同步周期为30秒。
      文件同步时间不可控，且缓冲区中堆积的数据会很多，数据安全性无法保证。
   3. everysec: 命令写入后aof_buf后调用系统write操作，write完成后线程返回。fsync同步文件操作由专门的线程每秒调用一次。性能和数据安全的平衡；默认配置。

3. 文件重写：定期重写AOF文件，达到压缩的目的。把进程内的数据转化为写命令同步到新的AOF文件。文件重写不是必须的。

   1. 过期的数据不再写入文件
   2. 无效的命令不再写入文件：重复设值、删除了的数据
   3. 多条命令可以合并为一个。为了防止单条命令过大造成客户端缓冲区溢出，对于list、set、hash、zset类型的key，  
      并不一定只使用一条命令；而是常量 REDIS_AOF_REWRITE_ITEMS_PER_CMD 为界将命令拆分多条。

   - 手动触发：直接调用bgrewriteaof命令，该命令的执行与bgsave有些类似；都是fork子进程进行具体的工作，在fork时阻塞。
   - 自动触发：两个条件同时满足时会触发bgrewriteaof命令
     1. auto-aof-rewrite-min-size：执行AOF重写时，文件的最小体积，默认64MB。
     2. auto-aof-rewrite-percentage: 执行AOF重写时，当前AOF大小（aof_current_size）和上一次重写时AOF大小（aof_base_size）的比值。

- bgrewriteaof 执行流程 重写由父进程fork子进程进行；重写期间执行的写命令需要追加到新的AOF文件中，引入了aof_rewrite_buf缓存。

![](/Users/lijingtang/workspace/study/study-tech/note/redis/img/bgrewriteaof.png)

1. 父进程首先判断是否正在执行bgsave/bgrewriteaof的子进程，如果存在bgrewriteaof则直接返回，存在bgsave命令则等bgsave执行完成后再执行。
2. 父进程执行fork操作创建子进程，这个过程父进程是阻塞的。
3. 1. 父进程fork后，bgrewriteaof命令返回 Background append only file rewrite started 信息并不再阻塞父进程，响应其他命令。Redis的所有写命令依然写入AOF缓存区，并根据appendfsync策略同步到硬盘保证原有AOF机制的正确。
   2. fork操作使用写时复制技术，子进程只能共享fork操作操作时的内存数据。由于父进程依然在响应命令，因此Redis使用AOF重写缓存区（aof_rewrite_buf）保存这部分数据，防止新AOF文件生成期间丢失这部分数据。bgrewriteaof执行期间，Redis的写命令同时追加到aof_buf和aof_rewirte_buf两个缓冲区。
4. 子进程根据内存快照，按照命令合并规则写入到新的AOF文件。
5. 1. 子进程写完新的AOF文件后，向父进程发信号，父进程更新统计信息。
   2. 父进程把AOF文件重写缓冲区的数据写入到新的AOF文件，保证了新AOF文件所保存的数据库状态和服务器当前状态一致。
   3. 使用新的AOF文件替换老文件，完成AOF重写。

- 启动时加载：当AOF开启，但AOF文件不存在时，即使RDB文件存在也不会加载。  
  aof-load-truncated 默认开启，AOF文件结尾不完整，日志警告，忽略掉文件的尾部，服务器启动成功。  
  因为Redis的命令只能在客户端上下文中执行，载入AOF文件之前，服务器会创建一个没有网路连接的客户端，执行AOF文件命令。

| 配置                        | 默认值                    | 说明                                                         |
| --------------------------- | ------------------------- | ------------------------------------------------------------ |
| save m n                    | 900 1 / 300 10 / 60 10000 | bgsave自动触发条件；如果没有save m n配置，相当于自动的RDB持久化关闭 |
| stop-writes-on-bgsave-error | yes                       | 当bgsave出现错误时，Redis是否停止执行写命令                  |
| rdbcompression              | yes                       | 是否开启RDB文件压缩                                          |
| rebchecksum                 | yes                       | 是否开启RDB文件验证；关闭大文件可以提升10%性能               |
| dbfilename                  | dump.rdb                  | RDB文件名                                                    |
| dir                         | ./                        | RDB文件和AOF文件所在目录                                     |
| appendonly                  | no                        | 是否开启AOF                                                  |
| appendfilename              | appendonly.aof            | AOF文件名                                                    |
| appendfsync                 | everysec                  | fsync持久化策略                                              |
| no-appendfsync-on-rewrite   | no                        | AOF重写期间是否禁止fsync                                     |
| auto-aof-rewrite-percentage | 100                       | 文件重写触发条件之一                                         |
| auto-aof-rewrite-min-size   | 64MB                      | 文件重写触发条件之一                                         |
| aof-load-truncated          | yes                       | AOF文件结尾损坏，Redistribution启动时是否仍载入AOF文件       |
| aof-use-rdb-preamble        | yes                       | 4.0以后，重写后文件前面的内容是rdb 混合方式                  |

RDB方式的优点是文件紧凑，体积小，网络传输快，适合全量复制；恢复速度比AOF快很多。对性能的影响相对较小。缺点是数据快照的持久化方式做不到实时，兼容性差。不支持拉链，只有一个dump.rdb。  
AOF方式的优点是在于支持秒级持久化、兼容性好。缺点是文件大、恢复速度慢，对性能影响大。  
在同一Redis实例中同时开启AOF和RDB方式的数据持久化方案也是可以的。重启时AOF文件将用于重建原始数据，因为AOF方式能最大限度保证数据的完整性。

## HA部署

单机问题：单点故障、容量有限、连接压力

### 主从复制

数据的复制是单向的，只能从主节点到从节点。从节点断开复制后，不会删除已有的数据，只是不再接受主节点新的数据变化。断开复制后，从节点又变回为主节点。

### 哨兵

- 主观下线：在心跳检测的定时任务中，如果其他节点超过一定时间没有回复，哨兵节点就会将其进行主观下线。
- 客观下线：哨兵节点主观下线后，会通过sentinel is-master-down-by-addr命令询问其他哨兵节点该主节点的状态；如果判断主节点下线的哨兵达到一定数量，则对该主节点进行客观下线。
- 在从节点中选择新的主节点原则是：首先过滤掉不健康的从节点；然后选择优先级最高的从节点（由slave-priority指定）；如果优先级无法区分，则选择复制偏移量最大的从节点；如果仍无法区分，则选择runid最小的从节点。

```
sentinel monitor {masterName} {masterIp} {masterPort} {quorum}：
    sentinel monitor是哨兵最核心的配置
    masterName指定了主节点名称，masterIp和masterPort指定了主节点地址，
    quorum是判断主节点客观下线的哨兵数量阈值：当判定主节点下线的哨兵数量达到quorum时，对主节点进行客观下线。建议取值为哨兵数量的一半加1
info sentinel：获取监控的所有主节点的基本信息
sentinel masters：获取监控的所有主节点的详细信息
sentinel master MyMaster：获取监控的主节点MyMaster的详细信息
sentinel slaves MyMaster：获取监控的主节点MyMaster的从节点详细信息
sentinel sentinels MyMaster：获取监控的主节点MyMaster的哨兵节点详细信息
sentinel get-master-addr-by-name MyMaster：获取监控的主节点MyMaster的地址信息
sentinel is-master-down-by-addr：哨兵节点之间通过该命令询问主节点是否下线，从而对是否客观下线做出判断
```

### 集群

只有主节点负责读写请求和集群信息的维护，从节点只进行主节点数据和状态信息的复制。  
当数据库中的16384个槽都分配了节点时，集群处于上线状态（ok）；如果有任意一个槽没有分配节点，则集群处于下线状态（fail）。  
带虚拟节点的一致性哈希分区：Redis集群使用的方案，其中的虚拟节点称为槽（slot），槽是介于数据和实际节点之间的虚拟概念；每个实际节点包含一定数量的槽，每个槽包含哈希值在一定范围内的数据。hash->槽->实际节点

与哨兵一样，**集群只实现了主节点的故障转移**；从节点故障时只会被下线，不会进行故障转移。因此，使用集群时，应谨慎使用读写分离技术，因为从节点故障会导致读服务不可用，可用性变差。  
单机 Redis 节点可以支持 16 个数据库，集群模式下只支持一个，即 db0。

Hash Tag 原理是：当一个 key 包含 {} 的时候，不对整个 key 做 hash，而仅对 {} 包括的字符串做hash。
Hash Tag 可以让不同的 key 拥有相同的 hash 值，从而分配在同一个槽里；这样针对不同 key 的批量操作(mget/mset 等)，以及事务、Lua 脚本等都可以支持。

复制是Redis高可用的基础，哨兵和集群都是在复制基础上实现高可用的。复制主要实现了数据的多机备份，以及对于读操作的负载均衡和简单的故障恢复。缺点是故障恢复无法自动化；写操作无法负载均衡，存储能力收到单机的限制。
在复制的基础上，哨兵实现了自动化的故障恢复。缺点是写操作无法负载均衡，存储能力受到单机的限制，无法对从节点进行故障转移。集群Redis解决了写操作无法负载均衡，以及存储能力收到单机限制的问题，实现了较为完善的高可用方案。

## 分布式锁

### 分布式锁原则

1. 互斥性
2. 防死锁
3. 自己解自己的锁
4. 容错性
5. 可重入

### 红锁

1. master
2. 延时启动

### 分布式锁降级

信号量 + mysql乐观锁