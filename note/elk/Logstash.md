> bin/logstash -e 'input{stdin{}}output{stdout{codec=>rubydebug}}'

logstash -f 方式运行的实例是一个管道 当要在一个实例中运行多个管道时可通过pipelines.yml实现 为更方便管理 配置文件中使用一个*.conf(input -> filter -> output)文件对应一个pipeline(即path.config字段)  
在不带参数的情况下启动logstash时默认读取pipelines.yml 并实例化其中指定的所有管道 当使用 -e 或 -f 启动时将忽略pipelines.yml 并记录警告 并且每个logstash的实例也意味着一个独立的JVM
#### conf
```
input {
 
  stdin { }
  
  beats {
    port => 5044
  }

}

filter {

  mutate {
    gsub => [ "[log][file][path]", "[\\]", "/" ]
    split => [ "[log][file][path]", "/" ]
    add_field => {
      "env" => "%{[log][file][path][3]}"
      "app" => "%{[log][file][path][4]}"
      "hostname" => "%{[log][file][path][5]}"
    }
    remove_field => [ "ecs", "input", "agent", "host", "tags", "log" ]
  }
  
  dissect {
    mapping => { "message" => "%{ts} %{+ts->} %{level} %{} --- [%{thread}] %{msg}" }
  }
  
  date {
    match => [ "ts", "ISO8601" ]
    target => "@timestamp"
    remove_field => [ "ts" ]
  }
  
  if [level] {
    mutate { strip => ["level"] }
  }
  if [message] {
    mutate { remove_field => [ "message" ] }
  }

}

output {

  stdout { codec => rubydebug }
  elasticsearch {
    hosts => ["192.168.3.111:9200", "192.168.3.112:9200", "192.168.3.113:9200"]
    index => "logstash-applog-%{+YYYY-MM-dd}"
  }
  
}
```