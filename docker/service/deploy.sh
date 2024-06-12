#!/bin/sh
cd `dirname $0`
# auto detect version
VERSION=`ls devops-service-*-launch-bin.tar.gz| awk -F"-" '{print $3}'`
# use the version param
if [ -n "$1" ]; then
    VERSION="$1";
fi
WORKDIR="/opt/devops"
CURRENTDIR=$(pwd)

docker stop devops-platform-service
docker rm devops-platform-service
docker run -p 8882:8882 -p 9200:9200 \
      -e LANG=zh_CN.UTF-8 \
      -e TZ=Asia/Shanghai \
      -e MYSQL_HOST=192.168.151.7 \
      -e REDIS_HOST=192.168.151.233 \
      -e MYSQL_PASSWORD=gvs@2021 \
      -e LOG_FILE_PATH="$CURRENTDIR"/logs \
      -v "$CURRENTDIR"/logs:$WORKDIR/logs \
      -v "$CURRENTDIR"/storage:$WORKDIR/storage \
      --restart=always \
      --name devops-platform-service \
      -d devops-platform-service:v"$VERSION"
docker logs -f devops-platform-service --tail 100

