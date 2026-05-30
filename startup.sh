#!/bin/bash

APP_NAME="gepspatial-carpooling-backend"
APP_DIR="/root/gepspatial-carpooling-backend"

JAR_PATH=$(find "$APP_DIR/target" -name "*.jar" ! -name "*original*" | head -1)

LOG_PATH="$APP_DIR/app.log"
PID_FILE="$APP_DIR/app.pid"

echo "Starting $APP_NAME..."

if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")

    if ps -p "$PID" > /dev/null 2>&1; then
        echo "$APP_NAME already running with PID $PID"
        exit 1
    else
        rm -f "$PID_FILE"
    fi
fi

nohup java -jar "$JAR_PATH" \
    --server.address=0.0.0.0 \
    --server.port=8080 \
    > "$LOG_PATH" 2>&1 &

echo $! > "$PID_FILE"

sleep 5

echo "Started."
echo "PID: $(cat "$PID_FILE")"