#!/bin/bash

# 添加组及用户
# groupadd -g 9200 elasticsearch
# useradd -s /bin/bash elasticsearch -g elasticsearch -u 9200
# chown -R elasticsearch:elasticsearch /apply/elasticsearch-7.5.2
# chown -R elasticsearch:elasticsearch /apply/data /apply/logs

# 系统参数调优：ES需要
#vim /etc/sysctl.conf
#vm.max_map_count=262144
#:wq
#sysctl -p
#sysctl -a|grep vm.max_map_count

export JAVA_HOME=/apply/jdk-11.0.6
export ES_HOME=/apply/elasticsearch-7.5.2
export NODE_HOME=/apply/node-v12.16.1
export PATH=$PATH:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:$JAVA_HOME/bin:$ES_HOME/bin:$NODE_HOME/bin

#启动ES
su -c '/apply/elasticsearch-7.5.2/bin/elasticsearch -d' elasticsearch
