#!/bin/bash

set -e

# 👉 Jenkins에서 전달된 환경 변수 사용
echo "📁 배포 디렉토리: $DEPLOY_DIR"
echo "📁 프로젝트 루트: $PROJECT_DIR"

echo "🚚 EC2에 배포 파일 전송..."

# 공통 배포 파일 전송
scp -i "$SSH_KEY" -o StrictHostKeyChecking=no \
  $DEPLOY_DIR/docker-compose.blue.yml \
  $DEPLOY_DIR/.env.prod \
  $DEPLOY_DIR/nginx/nginx.conf \
  $DEPLOY_DIR/redis/redis.conf \
  $DEPLOY_DIR/postgres/init.sql \
  $DEPLOY_DIR/promtail/config.yml \
  $EC2_USER@$EC2_HOST:/tmp/

# 각 서비스별 .env 전송 (필요한 경우만)
for service in gateway-service auth-service member-service device-service gesture-service gesture-sensor-service routine-service smartthings-service; do
  if [ -f "$PROJECT_DIR/$service/.env" ]; then
    echo "📦 $service/.env 전송"
    scp -i "$SSH_KEY" -o StrictHostKeyChecking=no "$PROJECT_DIR/$service/.env" "$EC2_USER@$EC2_HOST:/tmp/${service}.env"
  fi
done

echo "🚀 EC2에서 docker-compose 실행..."

ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no $EC2_USER@$EC2_HOST << EOF
  set -e

  sudo mkdir -p $PROJECT_DIR/{nginx,redis,postgres,promtail}

  # 설정 파일 이동
  sudo mv /tmp/docker-compose.blue.yml $PROJECT_DIR/docker-compose.yml
  sudo mv /tmp/.env.prod $PROJECT_DIR/.env.prod
  sudo mv /tmp/nginx.conf $PROJECT_DIR/nginx/nginx.conf
  sudo mv /tmp/redis.conf $PROJECT_DIR/redis/redis.conf
  sudo mv /tmp/init.sql $PROJECT_DIR/postgres/init.sql
  sudo mv /tmp/config.yml $PROJECT_DIR/promtail/config.yml

  # 서비스별 .env 이동
  for service in gateway-service auth-service member-service device-service gesture-service gesture-sensor-service routine-service smartthings-service; do
    if [ -f /tmp/\${service}.env ]; then
      sudo mkdir -p $PROJECT_DIR/\${service}
      sudo mv /tmp/\${service}.env $PROJECT_DIR/\${service}/.env
    fi
  done

  cd $PROJECT_DIR
  sudo docker-compose down
  sudo docker-compose --env-file .env.prod up -d
  sudo docker image prune -f
  echo "✅ Blue 배포 완료"
EOF