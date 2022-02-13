FROM ccr.ccs.tencentyun.com/gwy-base-image/skywalking-ui:8.5.0
# 作者联系方式
MAINTAINER "yangxl@sanweiguoye.onaliyun.com"

COPY docker-entrypoint.sh /skywalking/
RUN chmod 775 /skywalking/docker-entrypoint.sh

COPY logback.xml webapp/
COPY webapp.yml webapp/

ENTRYPOINT ["sh", "docker-entrypoint.sh"]