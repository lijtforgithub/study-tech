FROM ccr.ccs.tencentyun.com/gwy-base-image/kibana-oss:7.5.2
# 作者联系方式
MAINTAINER "yangxl@sanweiguoye.onaliyun.com"

COPY kibana.yml      /usr/share/kibana/config/kibana.yml

RUN mkdir -p /usr/share/kibana/data
COPY kibana.keystore  /usr/share/kibana/data/kibana.keystore

USER root
RUN chmod 775 /usr/share/kibana/data/kibana.keystore && chown kibana:root /usr/share/kibana/data/kibana.keystore

USER kibana
WORKDIR /usr/share/kibana