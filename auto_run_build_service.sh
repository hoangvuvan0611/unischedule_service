#!/bin/bash
set -e

APP_NAME="UniScheduleService"
IMAGE_NAME="unischeduleservice-image"
PORT=8801

echo "🧹 Xóa container cũ nếu có..."
docker rm -f $APP_NAME 2>/dev/null || true

echo "🧼 Xóa image cũ nếu có..."
docker rmi $IMAGE_NAME 2>/dev/null || true

echo "⬇️ Pull code mới từ Git..."
git pull origin main

echo "🐳 Build Docker image..."
docker build --no-cache -t $IMAGE_NAME .

echo "🚀 Chạy lại container..."
docker run -d \
  --name $APP_NAME \
  -p $PORT:8801 \
  $IMAGE_NAME

echo "✅ Deploy hoàn tất! App đang chạy tại http://localhost:$PORT"
