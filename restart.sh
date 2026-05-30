#!/bin/bash

APP_NAME="gepspatial-carpooling-backend"

echo "Restarting $APP_NAME..."

./stop.sh

sleep 5

./startup.sh

echo "$APP_NAME restarted successfully."