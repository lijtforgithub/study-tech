# 镜像
FROM ccr.ccs.tencentyun.com/gwy-base-image/canal-osadmin:1.1.4

MAINTAINER guowy (yangxinlun@sanweiguoye.onaliyun.com)

# install canal
COPY image/ /tmp/docker/
COPY canal.admin-*.tar.gz /home/admin/
COPY logback.xml /tmp/custom/

RUN \
    cp -R /tmp/docker/alidata /alidata && \
    chmod +x /alidata/bin/* && \
    mkdir -p /home/admin && \
    mkdir -p /home/admin/bin/ && \
    cp -R /tmp/docker/app_admin.sh /home/admin/app.sh  && \
    cp -R /tmp/docker/admin/* /home/admin/  && \
    /bin/cp -f alidata/bin/lark-wait /usr/bin/lark-wait && \

    mkdir -p /home/admin/canal-admin && \
    tar -xzvf /home/admin/canal.admin-*.tar.gz -C /home/admin/canal-admin && \
    /bin/rm -f /home/admin/canal.admin-*.tar.gz && \
    cp -f /tmp/custom/logback.xml /home/admin/canal-admin/conf/logback.xml  && \
    # 日志目录
    mkdir -p home/admin/canal-admin/logs  && \
    chmod +x /home/admin/*.sh  && \
    chmod +x /home/admin/bin/*.sh  && \
    chown admin: -R /home/admin && \
    yum clean all && \
    true

# 8089 web
EXPOSE 8089
WORKDIR /home/admin

ENTRYPOINT [ "/alidata/bin/main.sh" ]
CMD [ "/home/admin/app.sh" ]