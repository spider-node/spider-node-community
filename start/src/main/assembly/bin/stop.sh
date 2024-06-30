#!/bin/bash

SERVER_NAME='start-1.0.2'
JAR_NAME="$SERVER_NAME.jar"
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`

echo "spider-node stop ing"


echo "PID: $DEPLOY_DIR/lib/$JAR_NAME"
PIDS=`ps -f | grep java | grep "$DEPLOY_DIR/lib/$JAR_NAME" | awk '{print $2}'`
echo "PID: $PIDS"

kill -9 $PIDS

echo "spider-node stop suss"
