#!/bin/bash

set -e

# 👉 Jenkins에서 전달된 환경 변수 사용
echo "📁 배포 디렉토리: $DEPLOY_DIR"
echo "📁 프로젝트 루트: $PROJECT_DIR"

echo "🚚 EC2에 배포 파일 전송..."

# 공통 배포 파일 전송
scp -i "$SSH_KEY" -o StrictHostKeyChecking=no \
  $DEPLOY_DIR/docker-compose.blue.yml \
  $DEPLOY_DIR/nginx/nginx.conf \
  $DEPLOY_DIR/redis/redis.conf \
  $DEPLOY_DIR/prometheus/prometheus.yml \
  $DEPLOY_DIR/promtail/config.yml \
  $EC2_USER@$EC2_HOST:/tmp/

echo "🚀 EC2에서 docker-compose 실행..."

ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no $EC2_USER@$EC2_HOST << EOF
  set -e

  sudo mkdir -p $PROJECT_DIR/{nginx,redis,postgres,prometheus,promtail}

  # 설정 파일 이동
  sudo mv /tmp/docker-compose.blue.yml $PROJECT_DIR/docker-compose.yml
  sudo mv /tmp/nginx.conf $PROJECT_DIR/nginx/nginx.conf
  sudo mv /tmp/redis.conf $PROJECT_DIR/redis/redis.conf
  sudo mv /tmp/prometheus.conf $PROJECT_DIR/prometheus/prometheus.yml
  sudo mv /tmp/config.yml $PROJECT_DIR/promtail/config.yml

  cd $PROJECT_DIR
  sudo docker-compose down
  sudo docker-compose --env-file .env.prod up -d
  sudo docker image prune -f
  echo "✅ Blue 배포 완료"
EOF