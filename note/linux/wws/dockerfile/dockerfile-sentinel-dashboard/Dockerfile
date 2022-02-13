# 基础镜像
FROM ccr.ccs.tencentyun.com/gwy-base-image/base-java
MAINTAINER guowy (yangxinlun@sanweiguoye.onaliyun.com)

COPY gwowy-sentinel-dashboard-webapp.jar /opt/
EXPOSE 8080
ENTRYPOINT ["java","-jar","/opt/gwowy-sentinel-dashboard-webapp.jar"]
