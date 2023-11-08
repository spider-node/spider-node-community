
#!/bin/bash

SERVER_NAME='start-1.0.0-SNAPSHOT'
JAR_NAME="$SERVER_NAME.jar"
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf

### 删除rocksdb目录
rm -r /usr/local/rocksdb

#SERVER_PORT=`sed -nr '/port: [0-9]+/ s/.*port: +([0-9]+).*/\1/p' conf/application.yml`

PIDS=`ps -f | grep java | grep "$CONF_DIR" |awk '{print $2}'`
if [ "$1" = "status" ]; then
    if [ -n "$PIDS" ]; then
        echo "The $SERVER_NAME is running...!"
        echo "PID: $PIDS"
        exit 0
    else
        echo "The $SERVER_NAME is stopped"
        exit 0
    fi
fi

if [ -n "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME already started!"
    echo "PID: $PIDS"
    exit 1
fi

LOGS_DIR=$DEPLOY_DIR/logs
if [ ! -d $LOGS_DIR ]; then
    mkdir $LOGS_DIR
fi
STDOUT_FILE=$LOGS_DIR/stdout.log

JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "
JAVA_DEBUG_OPTS=""
if [ "$1" = "debug" ]; then
    JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
fi

JAVA_JMX_OPTS=""
if [ "$1" = "jmx" ]; then
    JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi

JAVA_MEM_OPTS=""
BITS=`java -version 2>&1 | grep -i 64-bit`
if [ -n "$BITS" ]; then
    JAVA_MEM_OPTS=" -server -Xmx4096m -Xms2048m -Xmn1024m -XX:PermSize=256m -Xss256k -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/applogs/system/error.dump"
else
    JAVA_MEM_OPTS=" -server -Xms2048m -Xmx512m -XX:PermSize=128m -XX:SurvivorRatio=2 -XX:+UseParallelGC "
fi

echo -e "Starting the $SERVER_NAME ..."
nohup java $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS -jar  $DEPLOY_DIR/lib/$JAR_NAME > $STDOUT_FILE 2>&1 &

echo "OK!"
PIDS=`ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $STDOUT_FILE"

tail -f /usr/local/spider-node/logs/stdout.log

