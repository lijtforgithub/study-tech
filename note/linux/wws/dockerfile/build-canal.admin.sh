#!/bin/bash

# 切换目录
cd dockerfile-canal.admin

# 登录harbor仓库并推送镜像
docker build -t ccr.ccs.tencentyun.com/gwy-middleware/canal-admin:1.1.4 .
docker login ccr.ccs.tencentyun.com -u 100012439416 -p asdefg123
docker push ccr.ccs.tencentyun.com/gwy-middleware/canal-admin:1.1.4
