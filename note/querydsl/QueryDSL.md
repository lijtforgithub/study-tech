#### MySQL
```
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>

@Bean
public MySQLQueryFactory sqlQueryFactory(DataSource dataSource){
    return new MySQLQueryFactory(() -> DataSourceUtils.getConnection(dataSource));
}


```