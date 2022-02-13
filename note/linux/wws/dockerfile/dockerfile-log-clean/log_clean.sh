#!/bin/bash

LOG_CLEAN_FILE=/apply/log_clean.log
echo "============================"`date +%Y-%m-%dT%H:%M:%S`"============================" >> $LOG_CLEAN_FILE

# 定义变量
LOG_RETENTION_DAYS=`cat /opt/log_retention_days.env`
echo "日志保留天数(环境变量):$LOG_RETENTION_DAYS" >> $LOG_CLEAN_FILE
LOG_RETENTION_DAYS=${LOG_RETENTION_DAYS:-30}
echo "日志保留天数(最终值):$LOG_RETENTION_DAYS" >> $LOG_CLEAN_FILE

echo "待清理的目录列表如下(环境变量):" >> $LOG_CLEAN_FILE
cat /opt/log_clean_dirs.env >> $LOG_CLEAN_FILE

for i in `cat /opt/log_clean_dirs.env`
do
  # 删除大日志文件
  echo "清理大日志文件开始:[$i]->[`find $i -name "*.log.*" -type f|wc -l`]个" >> $LOG_CLEAN_FILE
  find $i -name "*.log.*" -type f -exec rm -f {} \;

  # 删除[$LOG_RETENTION_DAYS]天未更新的日志文件
  echo "清理过期日志文件开始:[$i]->[`find $i -name "*.log" -type f -mtime +$LOG_RETENTION_DAYS|wc -l`]个" >> $LOG_CLEAN_FILE
  find $i -name "*.log" -type f -mtime +$LOG_RETENTION_DAYS -exec rm -f {} \;

  # 删除空目录
  echo "清理空目录开始:[$i]->[`find $i -type d -empty|wc -l`]个" >> $LOG_CLEAN_FILE
  find $i -type d -empty -exec rm -rf {} \;
done