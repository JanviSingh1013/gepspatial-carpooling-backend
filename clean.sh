#!/bin/bash

APP_NAME="gepspatial-carpooling-backend"
APP_DIR="/root/gepspatial-carpooling-backend"

cd $APP_DIR

echo "Cleaning and packaging $APP_NAME..."
./mvnw clean package -DskipTests

echo "Build complete. JAR is in target/"
ls -lh target/*.jar