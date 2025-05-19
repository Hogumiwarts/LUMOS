#!/bin/bash
set -e

COLOR=$1

if [[ "$COLOR" != "blue" && "$COLOR" != "green" ]]; then
  echo "❌ 사용법: ./deploy.sh [blue|green]"
  exit 1
fi

# ==========================================
# 프로젝트 디렉토리 정의
# ==========================================
PROJECT_DIR="/home/ubuntu/lumos"
COLOR_FILE="$PROJECT_DIR/scripts/current_color.txt"
NGINX_CONF="$PROJECT_DIR/nginx/nginx.conf"
NGINX_CONTAINER_NAME="lumos-nginx"

# ==========================================
# 배포 파일 이동
# ==========================================
echo "📁 배포 파일 이동 및 정리"
mkdir -p $PROJECT_DIR/{nginx,redis,prometheus,promtail,scripts}

sudo mv /tmp/docker-compose.*.yml $PROJECT_DIR/
sudo mv /tmp/nginx.conf $PROJECT_DIR/nginx/nginx.conf
sudo mv /tmp/redis.conf $PROJECT_DIR/redis/redis.conf
sudo mv /tmp/prometheus.template.yml $PROJECT_DIR/prometheus/prometheus.template.yml
sudo mv /tmp/prometheus.yml $PROJECT_DIR/prometheus/prometheus.yml
sudo mv /tmp/config.yml $PROJECT_DIR/promtail/config.yml
sudo mv /tmp/*.sh $PROJECT_DIR/scripts/
sudo chmod +x $PROJECT_DIR/scripts/*.sh

cd $PROJECT_DIR

# ==========================================
# 상태 파일이 없으면 초기화
# ==========================================
if [ ! -f "$COLOR_FILE" ]; then
  echo "blue" > "$COLOR_FILE"
fi

CURRENT_COLOR=$(cat "$COLOR_FILE")
PREV_COLOR=$([[ "$CURRENT_COLOR" == "blue" ]] && echo "green" || echo "blue")
PORT=$([[ "$PREV_COLOR" == "blue" ]] && echo 8081 || echo 8082)

echo "⏪ 색상 전환 시작: 현재 $CURRENT_COLOR → 배포 대상 $PREV_COLOR"

# ==========================================
# Docker 네트워크 확인
# ==========================================
echo "🌐 Docker 네트워크 확인"
sudo docker network ls | grep -q app-network || sudo docker network create app-network

# ==========================================
# 공용 인프라 서비스 실행
# ==========================================
echo "🧱 공용 인프라(infra + ingress) 실행"
sudo docker-compose \
  -f $PROJECT_DIR/docker-compose.infrastructure.yml \
  --env-file $PROJECT_DIR/.env.prod up -d

sudo docker-compose \
  -f $PROJECT_DIR/docker-compose.ingress.yml \
  --env-file $PROJECT_DIR/.env.prod up -d

# ==========================================
# 앱 실행 (새로운 버전)
# ==========================================
echo "▶ $PREV_COLOR 앱 실행..."
sudo docker-compose \
  --project-name "$PREV_COLOR" \
  -f "$PROJECT_DIR/docker-compose.$PREV_COLOR.yml" \
  --env-file "$PROJECT_DIR/.env.prod" up -d --no-recreate

# ==========================================
# readiness 체크
# ==========================================
echo "⏳ Gateway 준비 대기 중..."

until curl -skL http://localhost:$PORT/actuator/health | grep '"status":"UP"' > /dev/null && \
      [ "$(curl -skL -L -o /dev/null -w "%{http_code}" http://localhost:$PORT)" = "200" ]; do
  echo "   🔄 아직 gateway ($PREV_COLOR:$PORT) 준비 안 됨..."
  sleep 1
done

echo "✅ Gateway ($PREV_COLOR) 완전 기동 완료!"

# ==========================================
# Nginx 프록시 전환
# ==========================================
echo "🔄 프록시 전환 (switch.sh 실행)"
sudo bash "$PROJECT_DIR/scripts/switch.sh"

# ==========================================
# 프록시 응답 확인
# ==========================================
echo "🌐 프록시 응답 확인 중..."
until curl -skL http://localhost/actuator/health | grep -q '"status":"UP"'; do
  echo "   🔄 프록시 대상 응답 없음... 기다리는 중..."
  sleep 1
done

# ==========================================
# 이전 앱 종료
# ==========================================
echo "🧹 $CURRENT_COLOR 앱 종료 중..."
bash "$PROJECT_DIR/scripts/cleanup.sh" "$CURRENT_COLOR"

echo "🧹 이미지 정리 중..."
docker image prune -f
echo "✅ 정리 완료!"

echo "✅ 배포 완료: 현재 운영은 $PREV_COLOR 입니다."
