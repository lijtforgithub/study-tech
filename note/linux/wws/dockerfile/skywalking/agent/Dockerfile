FROM ccr.ccs.tencentyun.com/gwy-base-image/base-crontab:1.8
# 作者联系方式
MAINTAINER "yangxl@sanweiguoye.onaliyun.com"

# 复制SkyWalking的Agent包到每个Node
COPY agent-v8.5.0 /opt/skywalking-8.5.0/agent/
# 复制容器启动脚本
COPY start.sh /opt/
RUN chmod +x /opt/start.sh

ENTRYPOINT ["/opt/start.sh"]