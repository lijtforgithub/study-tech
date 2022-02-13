#!/bin/sh

# 从DockerHub官方拉取Canal相关的基础镜像
# 1.拉取[canal-server]基础镜像
docker pull canal/osbase:v2
docker tag canal/osbase:v2 ccr.ccs.tencentyun.com/gwy-base-image/canal-osbase:1.1.4

# 2.拉取[canal-admin]基础镜像
docker pull canal/osadmin:v1
docker tag canal/osadmin:v1 ccr.ccs.tencentyun.com/gwy-base-image/canal-osadmin:1.1.4

# 登录harbor仓库并推送镜像
docker login -u 100012439416 -p asdefg123 ccr.ccs.tencentyun.com
docker push ccr.ccs.tencentyun.com/gwy-base-image/canal-osbase:1.1.4
docker push ccr.ccs.tencentyun.com/gwy-base-image/canal-osadmin:1.1.4


# 从Elastic官方拉取Elastic相关的基础镜像
# 1.拉取[filebeat]基础镜像
docker pull docker.elastic.co/beats/filebeat:7.5.2
docker tag docker.elastic.co/beats/filebeat:7.5.2 ccr.ccs.tencentyun.com/gwy-base-image/filebeat:7.5.2

# 2.拉取[metricbeat]基础镜像
docker pull docker.elastic.co/beats/metricbeat:7.5.2
docker tag docker.elastic.co/beats/metricbeat:7.5.2 ccr.ccs.tencentyun.com/gwy-base-image/metricbeat:7.5.2

# 3.拉取[logstash]基础镜像
docker pull docker.elastic.co/logstash/logstash:7.5.2
docker tag docker.elastic.co/logstash/logstash:7.5.2 ccr.ccs.tencentyun.com/gwy-base-image/logstash:7.5.2

# 4.拉取[kibana-oss]基础镜像
docker pull docker.elastic.co/kibana/kibana-oss:7.5.2
docker tag docker.elastic.co/kibana/kibana-oss:7.5.2 ccr.ccs.tencentyun.com/gwy-base-image/kibana-oss:7.5.2

# 5.拉取Crontab基础镜像
docker pull primetoninc/jdk:1.8
docker tag primetoninc/jdk:1.8 ccr.ccs.tencentyun.com/gwy-base-image/base-crontab:1.8

# 6.拉取[SkyWalking-oap-server]及[SkyWalking-ui]基础镜像
docker pull apache/skywalking-oap-server:8.5.0-es7
docker tag apache/skywalking-oap-server:8.5.0-es7 ccr.ccs.tencentyun.com/gwy-base-image/skywalking-oap-server:8.5.0-es7
docker pull apache/skywalking-ui:8.5.0
docker tag apache/skywalking-ui:8.5.0 ccr.ccs.tencentyun.com/gwy-base-image/skywalking-ui:8.5.0

# 登录harbor仓库并推送镜像
docker login -u 100012439416 -p asdefg123 ccr.ccs.tencentyun.com
docker push ccr.ccs.tencentyun.com/gwy-base-image/filebeat:7.5.2
docker push ccr.ccs.tencentyun.com/gwy-base-image/metricbeat:7.5.2
docker push ccr.ccs.tencentyun.com/gwy-base-image/logstash:7.5.2
docker push ccr.ccs.tencentyun.com/gwy-base-image/kibana-oss:7.5.2
docker push ccr.ccs.tencentyun.com/gwy-base-image/base-crontab:1.8
docker push ccr.ccs.tencentyun.com/gwy-base-image/skywalking-oap-server:8.5.0-es7
docker push ccr.ccs.tencentyun.com/gwy-base-image/skywalking-ui:8.5.0