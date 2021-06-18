- 表很大，性能下降  
如果表有索引，增删改变慢。查询速度：1个或少量查询依然很快；并发大的时候会受到硬盘带宽影响速度。
> CREATE TABLE IF NOT EXISTS newTabName LIKE tabName
> SUBSTRING_INDEX(GROUP_CONCAT(activity_price ORDER BY id DESC), ',', 1)
## MYSQL
#### 基础层次
1. 客户端：向数据库发送请求（采用数据库连接池，减少频繁的开关连接）
2. 服务端
    1. 连接器: 控制用户的连接
    2. 分析器: 词法分析/语法分析（AST 抽象语法树）
    3. 优化器: 优化SQL语句，规定执行流程（可以查看SQL语句的执行计划，可以采用对应的优化点，来加快查询）
    4. 执行器: SQL语句的实际执行组件
3. 存储引擎：不同的存放位置，不同的文件格式
    1. InnoDB: 内存
    2. MyISAM: 磁盘
    3. Memory: 内存
- 优化
    * RBO 基于规则优化
    * CBO 基于成本优化
#### 性能监控