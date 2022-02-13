#!/bin/bash
/usr/sbin/crond

# 同一个Pod下不同进程共享文件系统,解决跨进程环境变量读取失败的问题(不同进程不共享环境变量)
for i in $LOG_CLEAN_DIRS
do
  echo $i >> /opt/log_clean_dirs.env
done
echo $LOG_RETENTION_DAYS > /opt/log_retention_days.env

# 防止进程退出
while true; do sleep 1; done
