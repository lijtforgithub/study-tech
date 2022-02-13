#!/bin/bash

# 切换目录
cd dockerfile-kibana

# 登录harbor仓库并推送镜像
docker build -t ccr.ccs.tencentyun.com/gwy-middleware/kibana-logs:7.5.2 .
docker login -u 100012439416 -p asdefg123 ccr.ccs.tencentyun.com
docker push ccr.ccs.tencentyun.com/gwy-middleware/kibana-logs:7.5.2
