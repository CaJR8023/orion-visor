#!/usr/bin/bash

# auto detect version
VERSION=`ls devops-service-*-launch-bin.tar.gz| awk -F"-" '{print $3}'`
# use the version param
if [ -n "$1" ]; then
    VERSION="$1";
fi

# compile context dir
CONTEXT_DIR=`pwd`
COMMAND="docker build -t devops-platform-service:v$VERSION -f $CONTEXT_DIR/Dockerfile $CONTEXT_DIR --build-arg VERSION="$VERSION" "

$COMMAND
docker tag devops-platform-service:v$VERSION devops-platform-service
