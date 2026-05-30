#!/bin/bash

APP_NAME="gepspatial-carpooling-backend"
APP_DIR="/root/gepspatial-carpooling-backend"

cd "$APP_DIR" || exit 1

echo "Cleaning and packaging $APP_NAME..."

mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "Build successful."
    echo "Generated JAR:"
    ls -lh target/*.jar
else
    echo "Build failed."
    exit 1
fi