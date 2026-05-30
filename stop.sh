#!/bin/bash

APP_NAME="gepspatial-carpooling-backend"
APP_DIR="/root/gepspatial-carpooling-backend"
PID_FILE="$APP_DIR/app.pid"

if [ -f "$PID_FILE" ]; then
  PID=$(cat $PID_FILE)
  echo "Stopping $APP_NAME with PID $PID..."
  kill -9 $PID
  rm -f $PID_FILE
  echo "$APP_NAME stopped."
else
  echo "$APP_NAME is not running (no PID file)."
fi