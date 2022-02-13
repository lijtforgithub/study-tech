# 添加组及用户
# groupadd -g 9200 elasticsearch
# useradd -s /sbin/nologin elasticsearch -g elasticsearch -u 9200
# chown -R elasticsearch:elasticsearch /apply/kibana-7.5.2
# chown -R elasticsearch:elasticsearch /apply/elasticsearch-7.5.2
# chown -R elasticsearch:elasticsearch /apply/data /apply/logs

# 系统参数调优：ES需要
#vim /etc/sysctl.conf
#vm.max_map_count=262144
#:wq
#sysctl -p
#sysctl -a|grep vm.max_map_count

#启动ES
su -c '/apply/elasticsearch-7.5.2/bin/elasticsearch -d' elasticsearch

#启动Kibana
su -c 'nohup /apply/kibana-7.5.2/bin/kibana &' elasticsearch

# groupadd -g 6379 redis
# useradd -s /sbin/nologin redis -g redis -u 6379
