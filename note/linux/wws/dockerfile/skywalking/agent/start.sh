#!/bin/bash
mkdir -p /apply/java_agents/skywalking/skywalking-8.5.0/agent
cp -rp /opt/skywalking-8.5.0/agent /apply/java_agents/skywalking/skywalking-8.5.0/
chmod 775 /apply/java_agents/skywalking/skywalking-8.5.0/agent/skywalking-agent.jar
cp -rfp /opt/skywalking-8.5.0/agent/config/*.config /apply/java_agents/skywalking/skywalking-8.5.0/agent/config/*.config

echo "----------------------------Successful----------------------------"

# 防止进程退出
#while true; do sleep 1; done

