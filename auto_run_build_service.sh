#!/bin/bash

APP_NAME="UniScheduleService"
IMAGE_NAME="unischeduleservice-image"
PORT=8801
NETWORK_NAME="agri-network"

echo "🧹 Xóa container cũ nếu có..."
docker rm -f $APP_NAME 2>/dev/null

echo "🧼 Xóa image cũ nếu có..."
docker rmi $IMAGE_NAME 2>/dev/null

echo "⬇️ Pull code mới từ Git..."
git pull origin main || git pull origin master

echo "🐳 Build Docker image..."
docker build -t $IMAGE_NAME .

echo "🚀 Chạy lại container..."
docker run -d \
  --name $APP_NAME \
  --network $NETWORK_NAME \
  -p $PORT:8801 \
  $IMAGE_NAME

echo "✅ Deploy hoàn tất! App đang chạy tại http://localhost:$PORT"