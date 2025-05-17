#!/bin/bash
set -e

echo "✅ EC2에 파일 전송 및 배포 시작..."

scp -i "$SSH_KEY_PATH" -o StrictHostKeyChecking=no \
  ./deploy/docker-compose.prod.yml \
  ./deploy/.env.prod \
  ./deploy/nginx/nginx.conf \
  ubuntu@$EC2_IP:/tmp/

ssh -i "$SSH_KEY_PATH" -o StrictHostKeyChecking=no ubuntu@$EC2_IP << EOF
  set -e
  sudo mkdir -p $PROJECT_DIR/nginx
  sudo mv /tmp/docker-compose.yml $PROJECT_DIR/docker-compose.yml
  sudo mv /tmp/.env.prod $PROJECT_DIR/.env.prod
  sudo mv /tmp/nginx.conf $PROJECT_DIR/nginx/nginx.conf
  cd $PROJECT_DIR
  sudo docker-compose down
  sudo docker-compose --env-file .env.prod up -d
  sudo docker image prune -f
EOF

echo "✅ 배포 완료"
