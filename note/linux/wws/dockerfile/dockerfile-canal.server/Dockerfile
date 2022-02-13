# 镜像
FROM ccr.ccs.tencentyun.com/gwy-base-image/canal-osbase:1.1.4

MAINTAINER agapple (yangxinlun@sanweiguoye.onaliyun.com)

# install canal
COPY image/ /tmp/docker/
COPY canal.deployer-*.tar.gz /home/admin/
COPY logback.xml /tmp/custom/
COPY canal.properties /tmp/custom/
COPY node_exporter-*.tar.gz /tmp/custom/

RUN \
    cp -R /tmp/docker/alidata /alidata && \
    chmod +x /alidata/bin/* && \
    mkdir -p /home/admin && \
    cp -R /tmp/docker/app.sh /home/admin/  && \
    cp -R /tmp/docker/admin/* /home/admin/  && \
    /bin/cp -f alidata/bin/lark-wait /usr/bin/lark-wait && \

    mkdir -p /home/admin/canal-server && \
    tar -xzvf /home/admin/canal.deployer-*.tar.gz -C /home/admin/canal-server && \
    /bin/rm -f /home/admin/canal.deployer-*.tar.gz && \
    cp -f /tmp/custom/logback.xml /home/admin/canal-server/conf/logback.xml  && \
    cp -f /tmp/custom/canal.properties /home/admin/canal-server/conf/canal.properties  && \
    /bin/rm -rf /home/admin/canal-server/conf/example && \

    tar zxvf /tmp/custom/node_exporter-*.tar.gz -C /home/admin && \
    ln -s /home/admin/node_exporter-0.18.1.linux-arm64 /home/admin/node_exporter && \
    /bin/rm -f /tmp/custom/node_exporter-*.tar.gz && \
    # 日志目录
    mkdir -p home/admin/canal-server/logs  && \
    chmod +x /home/admin/*.sh  && \
    chmod +x /home/admin/bin/*.sh  && \
    chown admin: -R /home/admin && \
    yum clean all && \
    true

# 11110 admin , 11111 canal , 11112 metrics, 9100 exporter
EXPOSE 11110 11111 11112 9100
WORKDIR /home/admin

ENTRYPOINT [ "/alidata/bin/main.sh" ]
CMD [ "/home/admin/app.sh" ]