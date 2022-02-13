#!/bin/bash

export JAVA_HOME=/apply/jdk1.8
export ZK_HOME=/apply/zookeeper-3.4.14
export KAFKA_HOME=/apply/kafka-2.2.2
export NODE_HOME=/opt/node-v12.16.1
export PATH=$PATH:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:$JAVA_HOME/bin:$ZK_HOME/bin:$KAFKA_HOME/bin:$NODE_HOME/bin

/apply/zookeeper-3.4.14/bin/zkServer.sh start /apply/zookeeper-3.4.14/conf/zoo.cfg
