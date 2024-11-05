## 逻辑架构

<img src="img/逻辑架构.png" style="zoom: 33%;" />

#### 客户端

向数据库发送请求（采用数据库连接池，减少频繁的开关连接）

### 服务端

Server 层包括连接器、查询缓存、分析器、优化器、执行器等，涵盖 MySQL 的大多数核心服务功能，以及所有的内置函数（如日期、时间、数学和加密函数等），所有跨存储引擎的功能都在这一层实现，比如存储过程、触发器、视图等。

1. 连接器: 控制用户的连接 权限验证  ```mysql -h 127.0.0.1 -P 3306 -u root```

   - 如果用户名密码认证通过，连接器会到权限表里面查出你拥有的权限。之后，这个连接里面的权限判断逻辑，都将依赖于此时读到的权限。

   - MySQL 在执行过程中临时使用的内存是管理在连接对象里面的。这些资源会在连接断开的时候才释放。（mysql_reset_connection >= 5.7）

     ```mysql
     -- 客户端如果太长时间没动静，连接器就会自动断开连接 28800秒/8小时
     show variables like 'wait_timeout';
     ```

     

2. 分析器: 词法分析/语法分析（AST 抽象语法树）

3. 优化器: 优化SQL语句，规定执行流程（可以查看SQL语句的执行计划，可以采用对应的优化点，来加快查询）

   * RBO 基于规则优化
   * CBO 基于成本优化

4. 执行器: SQL语句的实际执行组件（判断一下对表  有没有执行查询的权限）

存储引擎：不同的存放位置，不同的文件格式

1. InnoDB: 内存
2. MyISAM: 磁盘
3. Memory: 内存
4. Innodb：frm是表定义文件，ibd是数据文件
5. Myisam：frm是表定义文件，myd是数据文件，myi是索引文件

## 日志

#### InnoDB

1. 重做日志 redo log

   一组 4 个文件，每个文件的大小是 1GB。

   - 保证事务的原子性和持久性
   - 物理日志 页的物理修改操作
   - 循环写 空间固定会用完
   - 保证即使数据库发生异常重启，之前提交的记录都不会丢失，称为**crash-safe**。

2. 回滚日志 undo log

   - 保证事务一致性 
   - 逻辑日志 回滚到行记录到某个特定版本 根据每行进行记录

#### Server层

1. 归档日志 binlog

   因为最开始 MySQL 里并没有 InnoDB 引擎。MySQL 自带的引擎是 MyISAM，但是 MyISAM 没有 crash-safe 的能力，binlog 日志只能用于归档。只依靠 binlog 是没有 crash-safe 能力的。

   - 逻辑日志 sql语句 两阶段提交保证数据库使用binlog日志恢复的时候和当时的数据库状态一致。简单说，redo log 和 binlog 都可以用于表示事务的提交状态，而两阶段提交就是让这两个状态保持逻辑上的一致。
   - 追加写入 文件写到一定大小后会切换到下一个，并不会覆盖以前的日志。

   <img src="img/两阶段提交.png" style="zoom:33%;" />

 第一个成功第二个失败；先写redo log后写binlog恢复时会少一个事务；先写binlog后写redo log恢复时会多一个事务。

    ```mysql
    -- 设置成 1 表示每次事务的 redo log 都直接持久化到磁盘
    show variables like 'innodb_flush_log_at_trx_commit';
    -- 设置成 1 表示每次事务的 binlog 都持久化到磁盘
    show variables like 'sync_binlog'
    ```

## 隔离级别

在实现上，数据库里面会创建一个视图（MVCC非锁定读取），访问的时候以视图的逻辑结果为准。在“可重复读”隔离级别下，这个视图是在**事务启动**时创建的，整个事务存在期间都用这个视图。在“读提交”隔离级别下，这个视图是在**每个 SQL 语句开始执行**的时候创建的。这里需要注意的是，“读未提交”隔离级别下直接返回记录上的最新值，没有视图概念；而“串行化”隔离级别下直接用加锁的方式来避免并行访问。

```mysql
-- 查询事务隔离级别
show variables like 'transaction_isolation';
-- 在第一条select执行完后，才得到事务的一致性快照（所有select 都是以第一条为时间点）
START TRANSACTION;
-- 立即得到事务的一致性快照
START TRANSACTION WITH consistent snapshot;
```

#### 开启事务方式

1. 显式启动事务语句， begin 或 start transaction。配套的提交语句是 commit，回滚语句是 rollback。
2. set autocommit=0，这个命令会将这个线程的自动提交关掉。意味着如果你只执行一个 select 语句，这个事务就启动了，而且并不会自动提交。这个事务持续存在直到你主动执行 commit 或 rollback 语句，或者断开连接。（commit work and chain）

#### MVCC

## 锁



## 索引

#### 索引模型

1. 哈希表：适用于只有等值查询的场景
2. 有序数组：只适用于静态存储引擎（等值查询和范围查询场景中的性能就都非常优秀）
3. 二叉树：二叉树是搜索效率最高的，但是实际上大多数的数据库存储却并不使用二叉树。其原因是，索引不止存在内存中，还要写到磁盘上。为了让一个查询尽量少地读磁盘，就必须让查询过程访问尽量少的数据块。那么，我们就不应该使用二叉树，而是要使用“N 叉”树。这里，“N 叉”树中的“N”取决于数据块的大小。

#### InnoDB 的索引模型B+树

<img src="img/B+树.png" style="zoom:50%;" />

假设非叶子节点的键值为 K1,K2,…,Kk，对应的子节点指针为 P0,P1,…,Pk，那么这些键值和指针的关系如下：

- P0指向的子树中的所有键值都小于 K1。
- P1指向的子树中的所有键值都大于等于 K1 且小于 K2。
- P2指向的子树中的所有键值都大于等于 K2且小于 K3。
- ...
- Pk−1 指向的子树中的所有键值都大于等于 Kk−1 且小于 Kk。
- Pk指向的子树中的所有键值都大于等于 Kk。

> B+ 树为了维护索引有序性；会发生也分裂和页合并。自增主键的插入数据模式，每次插入一条新记录，都是追加操作，都不涉及到挪动其他记录，也不会触发叶子节点的分裂。

###### 聚簇索引和二级索引

- 主键索引的叶子节点存的是整行数据。在 InnoDB 里，主键索引也被称为聚簇索引（clustered index）。
- 非主键索引的叶子节点内容是主键的值。在 InnoDB 里，非主键索引也被称为二级索引（secondary index）。
- 主键长度越小，普通索引的叶子节点就越小，普通索引占用的空间也就越小。

###### B树和B+树的区别

- B数据的节点键值不重复。

- B树的每个节点都存储了key和data，而B+树的data存储在叶子节点上。 B+树非叶子节点仅存储key不存储data，这样一个节点就可以存储更多的key，可以使得B+树相对B树来说更矮（IO次数就是树的高度），所以与磁盘交换的IO操作次数更少。
- B+树所有叶子节点构成一个有序链表，按主键排序来遍历全部记录，能更好支持范围查找。由于数据顺序排列并且相连，所以便于区间查找和搜索。而B树则需要进行每一层的递归遍历，相邻的元素可能在内存中不相邻，所以缓存命中性没有B+树好。
- B+树所有的查询都要从根节点查找到叶子节点，查询性更稳定；而B树，每个节点都可能查找到数据，需要在叶子节点和内部节点不停的往返移动，所以不稳定。

## 知识点

- 表很大 性能下降：  如果表有索引：增删改变慢；查询1个或少量查询依然很快；并发大的时候会受到硬盘带宽影响速度。

## 优化

- 不要使用select * （避免回表）
- 不要使用长事务（长事务意味着系统里面会存在很老的事务视图；大量占用存储空间）
- 覆盖索引
- 最左前缀原则
- 索引下推 ICP
- 深翻页 join自己
- force index
- 关联表 关联字段要有索引 且类型一致
- 对索引字段做函数操作，可能会破坏索引值的有序性，因此优化器就决定放弃走树搜索功能。
- MRR 因为大多数的数据都是按照主键递增顺序插入得到的，所以我们可以认为，如果按照主键的递增顺序查询的话，对磁盘的读比较接近顺序读，能够提升读性能。
- 索引合并（同一张表两个条件都有索引；OR索引取并集；AND索引取交集）
- 在删除数据的时候尽量加 limit。这样不仅可以控制删除数据的条数，让操作更安全，还可以减小加锁的范围。
- 如果你的事务中需要锁多个行，要把最可能造成锁冲突、最可能影响并发度的锁的申请时机尽量往后放。可以考虑通过将一行改成逻辑上的多行来减少锁冲突。比如 10 个记录，这样每次冲突概率变成原来的 1/10，可以减少锁等待个数，也就减少了死锁检测的 CPU 消耗。
- change buffer 只限于用在普通索引的场景下，而不适用于唯一索引。写多读少，效果最好。写入之后马上会做查询，即使满足了条件，将更新先记录在 change buffer，但之后由于马上要访问这个数据页，会立即触发 merge 过程。change buffer 反而起到了副作用。
- 间隙锁的引入，可能会导致同样的语句锁住更大的范围，这其实是影响了并发度的。隔离级别读提交加 binlog_format=row 的组合。业务不需要可重复读的保证。
- 如果你的需求并不需要对结果进行排序，那你可以在 SQL 语句末尾增加 order by null。```select id%10 as m, count(*) as c from t1 group by m order by null;```
- 更新数据都是先读后写的，而这个读，只能读当前的值，称为“当前读”（current read）。
- redo log 主要节省的是随机写磁盘的 IO 消耗（转成顺序写），而 change buffer 主要节省的则是随机读磁盘的 IO 消耗。
- 在数据库设计中，我们非常强调定长存储，因为定长存储的性能更好。
- select * from table where coloum = '' for update (如果coloum列是唯一索引，查到数据是行锁，查不到是间隙锁；如果coloum列是普通索引，查不查到数据都是间隙锁；如果coloum列没有索引，是表锁)
- https://gitee.com/bearkang/mysql-optimization
## 常用SQL
```sql
CREATE TABLE IF NOT EXISTS newTabName LIKE tabName;
SUBSTRING_INDEX(GROUP_CONCAT(activity_price ORDER BY id DESC), ',', 1)

-- 批量更新
UPDATE mytable 
    SET myfield = CASE id 
        WHEN 1 THEN 'value1'
        WHEN 2 THEN 'value2'
        WHEN 3 THEN 'value3'
    END
WHERE id IN (1, 2, 3);

-- 统计表字段
SELECT a.COLUMN_NAME 字段名,  a.COLUMN_TYPE 类型, a.COLUMN_COMMENT 说明
FROM information_schema.COLUMNS a
WHERE a.TABLE_SCHEMA = 'base' AND a.TABLE_NAME = 'bus_project';
-- 查询有指定字段的表
SELECT
    TABLE_SCHEMA AS 'Database',
        TABLE_NAME AS 'Table'
FROM INFORMATION_SCHEMA.COLUMNS
WHERE
    COLUMN_NAME = 'area_company_id';

SELECT
    t1.customer_mobile, t2.*
FROM
    ques_questionnaire_2023 t1
        JOIN (
        SELECT
            *
        FROM
            sp_send_record_2023
        WHERE
                ( questionnaire_uuid, create_time ) IN ( SELECT questionnaire_uuid, MAX( create_time ) FROM sp_send_record_2023 GROUP BY questionnaire_uuid )) t2 ON t1.uuid = t2.questionnaire_uuid
WHERE
    t1.customer_mobile LIKE '00%'
```

```sql
-- 慢查询日志 设置之后 断开连接重新连
SHOW VARIABLES LIKE 'log_output';
SET GLOBAL log_output = 'TABLE';
    
SHOW VARIABLES LIKE 'slow_query_log';
SET GLOBAL slow_query_log = 'ON';

SHOW GLOBAL VARIABLES LIKE 'long_query_time';
SET GLOBAL long_query_time = 0;

SELECT l.start_time, l.user_host, l.query_time, l.lock_time, CONVERT(l.sql_text USING utf8) as `sql`, l.thread_id
FROM mysql.slow_log l
-- WHERE l.thread_id IN (121, 126)
ORDER BY start_time DESC LIMIT 50;
-- 磁盘IO
SHOW VARIABLES LIKE '%innodb_io_capacity%';
-- 是否刷邻页
SHOW VARIABLES LIKE '%innodb_flush_neighbors%';
-- 重新统计索引信息
ANALYZE TABLE test; 
-- 重建表
ALTER TABLE test ENGINE = INNODB;
-- 大于60s的长事务
SELECT * FROM information_schema.innodb_trx WHERE TIME_TO_SEC(timediff(now(), trx_started)) > 60;
-- 死锁检测
SHOW VARIABLES LIKE 'innodb_deadlock_detect';
-- 死锁超时时间
SHOW VARIABLES LIKE 'innodb_lock_wait_timeout';
```
## 全文索引
```sql
ALTER TABLE 表名 ADD FULLTEXT INDEX 索引名称 (字段1,字段2,字段3) WITH PARSER ngram;

show VARIABLES like 'ngram_token_size';
my.ini文件下的 [mysqld] 下面加上 ngram_token_size = 2

SELECT * FROM 表名 WHERE MATCH(列名1,列名2) AGAINST(检索内容1 检索内容2);
```
## 窗口函数
![](img/窗口函数.png)
> 函数 OVER ([PARTITION BY 字段名 ORDER BY 字段名 ASC|DESC])  
> 函数 OVER 窗口名 … WInDOW 窗口名 AS ([PARTITION BY 字段名 ORDER BY 字段名 ASC|DESC])

```sql
CREATE TABLE `goods` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category_id` int NOT NULL,
  `category` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `stock` int NOT NULL,
  `upper_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO goods ( category_id, category, NAME, price, stock, upper_time )
VALUES
    ( 1, '女装/女士精品', 'T恤', 39.90, 1000, '2020-11-10 00:00:00' ),
    ( 1, '女装/女士精品', '连衣裙', 79.90, 2500, '2020-11-10 00:00:00' ),
    ( 1, '女装/女士精品', '卫衣', 89.90, 1500, '2020-11-10 00:00:00' ),
    ( 1, '女装/女士精品', '牛仔裤', 89.90, 3500, '2020-11-10 00:00:00' ),
    ( 1, '女装/女士精品', '百褶裙', 29.90, 500, '2020-11-10 00:00:00' ),
    ( 1, '女装/女士精品', '呢绒外套', 399.90, 1200, '2020-11-10 00:00:00' ),
    ( 2, '户外运动', '自行车', 399.90, 1000, '2020-11-10 00:00:00' ),
    ( 2, '户外运动', '山地自行车', 1399.90, 2500, '2020-11-10 00:00:00' ),
    ( 2, '户外运动', '登山杖', 59.90, 1500, '2020-11-10 00:00:00' ),
    ( 2, '户外运动', '骑行装备', 399.90, 3500, '2020-11-10 00:00:00' ),
    ( 2, '户外运动', '运动外套', 799.90, 500, '2020-11-10 00:00:00' ),
    ( 2, '户外运动', '滑板', 499.90, 1200, '2020-11-10 00:00:00' );
```
1. 序号函数
```sql
SELECT *, 
	    ROW_NUMBER() OVER (PARTITION BY category_id ORDER BY price DESC) AS row_num
FROM goods;

SELECT *,
       RANK() OVER (PARTITION BY category_id ORDER BY price DESC) AS topPrice
FROM goods;

SELECT *,
       DENSE_RANK() OVER (PARTITION BY category_id ORDER BY price DESC) AS topPrice
FROM goods;


SELECT * FROM (
                  SELECT *,
                         ROW_NUMBER() OVER (PARTITION BY category_id ORDER BY price DESC) AS topPrice
                  FROM goods) tmp
WHERE tmp.topPrice <= 3;

SELECT *,
       DENSE_RANK() OVER (PARTITION BY category_id ORDER BY price DESC) AS topPrice
FROM goods
WHERE category_id = 1
    LIMIT 4;
```
2. 分布函数
```sql
SELECT
	RANK() OVER (PARTITION BY category_id ORDER BY price DESC) AS r,
	PERCENT_RANK() OVER (PARTITION BY category_id ORDER BY price DESC) AS pr,
	id, category_id, category, name, price, stock
FROM goods
WHERE category_id = 1;

SELECT
    RANK() OVER w AS r,
    PERCENT_RANK() OVER w AS pr,
    id, category_id, category, name, price, stock
FROM goods
WHERE category_id = 1 
WINDOW w AS (PARTITION BY category_id ORDER BY price DESC);

SELECT
    CUME_DIST() OVER (PARTITION BY category_id ORDER BY price DESC) AS cd,
    id, category_id, category, name, price, stock
FROM goods;
```
3. 前后函数
```sql
SELECT *,
	LAG(price, 1) OVER (PARTITION BY category_id ORDER BY price DESC) AS pre_price
FROM goods;

SELECT *, price - pre_price
FROM
(
    SELECT *, LAG(price, 1) OVER w AS pre_price
    FROM goods
    WINDOW w AS (PARTITION BY category_id ORDER BY price DESC)
) AS tmp;

SELECT *, LEAD(price, 1) OVER w AS post_price
FROM goods
WINDOW w AS (PARTITION BY category_id ORDER BY price DESC);
```
4. 首位函数
```sql
SELECT *,
	FIRST_VALUE(price) OVER (PARTITION BY category_id ORDER BY price DESC) AS max_price
FROM goods;
```
5. 其他函数
```sql
SELECT *,
    NTH_VALUE(price, 2) OVER w AS second_price,
    NTH_VALUE(price, 3) OVER w AS third_price  
FROM goods
WINDOW w AS (PARTITION BY category_id ORDER BY price);

SELECT NTILE(3) OVER w AS nt, id, category, name, price
FROM goods
WINDOW w AS (PARTITION BY category_id ORDER BY price);
```
## 安装
```shell
# 上传gz包到/opt
cd /opt/
tar -xvf mysql-5.7.40-linux-glibc2.12-x86_64.tar.gz
ln -s mysql-5.7.40-linux-glibc2.12-x86_64 mysql
cd mysql

groupadd mysql
useradd -r -g mysql -s /bin/false mysql
mkdir data
chown -R root:root .

bin/mysqld --initialize --user=mysql --basedir=/opt/mysql --datadir=/opt/mysql/data
bin/mysql_ssl_rsa_setup --datadir=/opt/mysql/data

cd /var/log/
mkdir mysql
chown -R mysql:mysql mysql/

# 编辑配配置文件
vi /etc/my.cnf
basedir=/opt/mysql
datadir=/opt/mysql/data
socket=/var/log/mysql/mysql.sock
log-error=/var/log/mysql/error.log
pid-file=/var/log/mysql/mysql.pid

ln -s /var/log/mysql/mysql.sock /tmp/mysql.sock

# 启动服务
bin/mysqld_safe --user=mysql &

bin/mysql --user=root --password=临时密码 日志里有
# 修改密码和开放权限
set password=password('admin');
grant all privileges on *.* to root@'%' identified by 'admin';
flush privileges;

# 开放防火墙端口
firewall-cmd --zone=public --add-port=3306/tcp --permanent
firewall-cmd --reload
firewall-cmd --list-ports

# 关闭
bin/mysqladmin --user=root --password shutdown

# 开机启动
cp /opt/mysql/support-files/mysql.server /etc/init.d/mysql
chmod +x /etc/init.d/mysql
chkconfig --add mysql
```

## 备份

```sh
# mysqlback.sh
/usr/local/mysql/bin/mysqldump -h127.0.0.1 -uadmin -pBAFfsGD5FNv134A --port=3306 --all-databases -F | gzip >> /run/media/root/db-new/backup/$(date +%Y%m%d_%H%M%S).sql.gz

10 01 * * * source /run/media/root/db-new/backup/mysqlback.sh
```

