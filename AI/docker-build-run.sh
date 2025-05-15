#!/bin/bash

set -e

IMAGE_NAME="gesture-classification"
CONTAINER_NAME="lumos-gesture-classification"

# 경로는 슬래시(/) 또는 따옴표로 명확히 처리
HOST_PATH="C:/SSAFY/PJT/S12P31D103/AI/app"

# 1. 기존 컨테이너 정리
docker stop $CONTAINER_NAME 2>/dev/null || true
docker rm $CONTAINER_NAME 2>/dev/null || true

# 2. 이미지 빌드 (필요 시)
docker build -t $IMAGE_NAME .

# 3. 컨테이너 실행
docker run -d --name $CONTAINER_NAME -p 8000:8000 -v "$HOST_PATH":/app/app --gpus all $IMAGE_NAME
