FROM ccr.ccs.tencentyun.com/gwy-base-image/skywalking-oap-server:8.5.0-es7
# 作者联系方式
MAINTAINER "yangxl@sanweiguoye.onaliyun.com"

COPY docker-entrypoint.sh /skywalking/
RUN chmod 775 /skywalking/docker-entrypoint.sh

COPY alarm-settings.yml /skywalking/config/
COPY log4j2.xml /skywalking/config/

ENTRYPOINT ["sh", "docker-entrypoint.sh"]