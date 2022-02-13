FROM ccr.ccs.tencentyun.com/gwy-base-image/logstash:7.5.2
# 作者联系方式
MAINTAINER "duxiuqian@sanweiguoye.onaliyun.com"

ENV TZ=Asia/Shanghai
COPY log4j2.properties /usr/share/logstash/config/log4j2.properties
COPY logstash.yml      /usr/share/logstash/config/logstash.yml
COPY jvm.options       /usr/share/logstash/config/jvm.options
COPY logstash.conf     /usr/share/logstash/pipeline/logstash.conf
USER root
