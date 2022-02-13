FROM ccr.ccs.tencentyun.com/gwy-base-image/base-crontab:1.8
# 作者联系方式
MAINTAINER "yangxl@sanweiguoye.onaliyun.com"

# 复制容器启动脚本
COPY start.sh /opt/start.sh
# 复制日志清理脚本
COPY log_clean.sh /opt/log_clean.sh
# 定时任务：每隔1天运行一次,删除过期日志
RUN chmod +x /opt/start.sh && chmod +x /opt/log_clean.sh && yum -y install crontabs && echo "1 2 * * * /opt/log_clean.sh" >> /var/spool/cron/root

ENTRYPOINT ["/opt/start.sh"]