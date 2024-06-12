#!/bin/bash

# project name
# shellcheck disable=SC2154
SERVER_NAME="${project.build.finalName}"
# jar name
JAR_NAME="${project.build.finalName}.jar"

# root path dir
DEPLOY_DIR=`pwd`
# JVM Configuration
JAVA_OPTS=" -Doracle.jdbc.timezoneAsRegion=false"

# JVM Configuration
JAVA_MEM_OPTS=" -server -XX:SurvivorRatio=6 -XX:+UseParallelGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$LOGS_DIR"

JAR="$DEPLOY_DIR/$JAR_NAME"

echo -e "Starting the  $SERVER_NAME ..."
nohup java $JAVA_OPTS $JAVA_MEM_OPTS -jar $JAR --spring.profiles.active=prod 2>&1 &
nginx -g 'daemon off;'