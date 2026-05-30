#!/bin/bash

APP_NAME="gepspatial-carpooling-backend"
APP_DIR="/root/gepspatial-carpooling-backend"
JAR_PATH="$APP_DIR/target/gepspatial-carpooling-backend-0.0.1-SNAPSHOT.jar"
LOG_PATH="$APP_DIR/app.log"
PID_FILE="$APP_DIR/app.pid"

echo "Starting $APP_NAME..."

if [ -f "$PID_FILE" ]; then
  PID=$(cat "$PID_FILE")

  if ps -p "$PID" > /dev/null 2>&1; then
    echo "$APP_NAME is already running with PID $PID"
    exit 1
  else
    echo "Removing stale PID file..."
    rm -f "$PID_FILE"
  fi
fi

nohup java -jar "$JAR_PATH" \
  --server.address=0.0.0.0 \
  --server.port=8080 \
  > "$LOG_PATH" 2>&1 &

echo $! > "$PID_FILE"

sleep 2

if ps -p $(cat "$PID_FILE") > /dev/null 2>&1; then
  echo "$APP_NAME started successfully."
  echo "PID: $(cat "$PID_FILE")"
  echo "Logs: $LOG_PATH"
else
  echo "Failed to start $APP_NAME. Check logs:"
  tail -50 "$LOG_PATH"
  rm -f "$PID_FILE"
fi