#!/bin/bash

APP_NAME="UniScheduleService"
IMAGE_NAME="UniScheduleService-image"
PORT=8801
NETWORK_NAME="agri-network"
FLASK_CONTAINER="flask-recommender"

echo "🧹 Xóa container cũ nếu có..."
docker rm -f $APP_NAME 2>/dev/null

echo "🧼 Xóa image cũ nếu có..."
docker rmi $IMAGE_NAME 2>/dev/null

echo "⬇️ Pull code mới từ Git..."
git pull origin main || git pull origin master

echo "🐳 Build Docker image..."
docker build -t $IMAGE_NAME .

echo "🚀 Chạy lại container..."

echo "✅ Deploy hoàn tất! App đang chạy tại http://localhost:$PORT"