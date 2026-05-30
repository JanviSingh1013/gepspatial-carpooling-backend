#!/bin/bash

APP_NAME="gepspatial-carpooling-backend"
APP_DIR="/root/gepspatial-carpooling-backend"

PID_FILE="$APP_DIR/app.pid"

if [ ! -f "$PID_FILE" ]; then
    echo "$APP_NAME not running."
    exit 0
fi

PID=$(cat "$PID_FILE")

echo "Stopping $APP_NAME ($PID)..."

kill "$PID"

sleep 5

if ps -p "$PID" > /dev/null 2>&1; then
    echo "Force stopping..."
    kill -9 "$PID"
fi

rm -f "$PID_FILE"

echo "Stopped."