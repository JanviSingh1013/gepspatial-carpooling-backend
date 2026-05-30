#!/bin/bash

APP_NAME="gepspatial-carpooling-backend"
APP_DIR="/root/gepspatial-carpooling-backend"
JAR_PATH="$APP_DIR/target/backend-0.0.1-SNAPSHOT.jar"
LOG_PATH="$APP_DIR/app.log"
PID_FILE="$APP_DIR/app.pid"

echo "Starting $APP_NAME..."

if [ -f "$PID_FILE" ]; then
  echo "$APP_NAME is already running with PID $(cat $PID_FILE). Stop it first."
  exit 1
fi

nohup java -jar $JAR_PATH \
  --server.address=0.0.0.0 \
  --server.port=8089 > $LOG_PATH 2>&1 &

echo $! > $PID_FILE
echo "$APP_NAME started with PID $(cat $PID_FILE). Logs: $LOG_PATH"