#!/bin/bash
export AUTH_TYPE=basic
export BASIC_AUTH_USER=admin
export BASIC_AUTH_PWD=123456

bin/cerebro -Dhttp.port=9400 -Dhttp.address=192.168.100.7
