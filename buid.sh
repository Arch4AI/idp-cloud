#!/bin/sh
mvn clean package -DskipTests
docker rmi ccr.ccs.tencentyun.com/f2ccloud/cloud-any-login:dev
docker build --platform linux/amd64 -t ccr.ccs.tencentyun.com/f2ccloud/cloud-any-login:dev .

#docker buildx build --platform linux/amd64 -t ccr.ccs.tencentyun.com/f2ccloud/cloud-any-login --push .
