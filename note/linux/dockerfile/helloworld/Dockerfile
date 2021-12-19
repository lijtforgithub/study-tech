FROM tomcat:8.5.46-jdk8-openjdk
MAINTAINER lijt
# cd /usr/lal/tomcat/webapps 切换工作目录 不存在则创建
WORKDIR /usr/local/tomcat/webapps
# 复制目录下的所有文件到容器目录
ADD docker-web ./docker-web