#!/bin/bash

# 切换目录
cd agent

# 登录harbor仓库并推送镜像
docker build -t ccr.ccs.tencentyun.com/gwy-middleware/skywalking-agent:8.5.0 .
docker login -u 100012439416 -p asdefg123 ccr.ccs.tencentyun.com
docker push ccr.ccs.tencentyun.com/gwy-middleware/skywalking-agent:8.5.0
