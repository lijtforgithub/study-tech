#!/bin/bash

# 切换目录
cd oap

# 登录harbor仓库并推送镜像
docker build -t ccr.ccs.tencentyun.com/gwy-middleware/skywalking-oap-server:8.5.0-es7 .
docker login -u 100012439416 -p asdefg123 ccr.ccs.tencentyun.com
docker push ccr.ccs.tencentyun.com/gwy-middleware/skywalking-oap-server:8.5.0-es7
