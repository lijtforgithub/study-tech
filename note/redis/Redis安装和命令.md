## 安装
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
```

## 命令
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
```
#### key
命令 | 描述
---|---
del KEY1 [KEY2 ...] | 该命令用于在 key 存在时删除 key
dump KEY | 序列化给定 key ，并返回被序列化的值
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
#### string
1. 二进制安全

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
1. 栈 同向命令
2. 队列 反向命令
3. 数组
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
1. srandmember：count 为正数返回不重复、不会超过已有集合的数据；为负数返回会重复、满足指定数量的数据。

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