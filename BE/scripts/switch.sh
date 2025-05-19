#!/bin/bash

set -e

# ======================
# 설정
# ======================
COLOR_FILE="./scripts/current_color.txt"
NGINX_CONF="./nginx/nginx.conf"
NGINX_CONTAINER_NAME="lumos-nginx"
GATEWAY_SERVICE_PREFIX="lumos-gateway-service"

# ======================
# 유효성 검사
# ======================
if [ ! -f "$COLOR_FILE" ]; then
  echo "blue" > "$COLOR_FILE"
fi

if [ ! -f "$NGINX_CONF" ]; then
  echo "❌ nginx.conf 파일이 없습니다: $NGINX_CONF"
  exit 1
fi

CURRENT_COLOR=$(cat "$COLOR_FILE")

# ======================
# 살아있는 gateway 확인
# ======================
gateway_exists() {
  sudo docker ps --format '{{.Names}}' | grep -q "$1"
}

if gateway_exists "${GATEWAY_SERVICE_PREFIX}-green"; then
  TARGET_COLOR="green"
elif gateway_exists "${GATEWAY_SERVICE_PREFIX}-blue"; then
  TARGET_COLOR="blue"
else
  echo "❌ gateway 서비스가 모두 꺼져 있어 전환할 수 없습니다."
  exit 1
fi

if [ "$CURRENT_COLOR" == "$TARGET_COLOR" ]; then
  echo "ℹ️ 현재 프록시 상태($CURRENT_COLOR)와 동일하므로 전환하지 않습니다."
  exit 0
fi

echo "▶ Switching from $CURRENT_COLOR to $TARGET_COLOR"

# ======================
# nginx.conf 내 프록시 대상 강제 변경
# ======================
sudo sed -i "s|server lumos-gateway-service-[a-z]\+:8080;|server lumos-gateway-service-${TARGET_COLOR}:8080;|" "$NGINX_CONF"

# 변경 확인 로그
echo "📝 nginx.conf 프록시 대상 변경됨:"
grep "server lumos-gateway-service" "$NGINX_CONF"

# ======================
# nginx reload
# ======================
if sudo docker ps --format '{{.Names}}' | grep -q "$NGINX_CONTAINER_NAME"; then
  echo "🔍 nginx 설정 파일 문법 검사"
  if ! sudo docker exec "$NGINX_CONTAINER_NAME" nginx -t; then
    echo "❌ nginx 설정 문법 오류! 재시작 중단"
    exit 1
  fi

  echo "🔄 Restarting Nginx"
  sudo docker restart "$NGINX_CONTAINER_NAME"
else
  echo "❌ Nginx 컨테이너 $NGINX_CONTAINER_NAME 가 존재하지 않습니다."
  exit 1
fi

# ======================
# 상태 기록 및 응답 확인
# ======================
echo "$TARGET_COLOR" | sudo tee "$COLOR_FILE" > /dev/null

echo "⏳ 프록시 전환 후 $TARGET_COLOR 응답 대기 중..."

RETRY=0
MAX_RETRY=60
while ! curl -skL -o /dev/null -w "%{http_code}" http://localhost/ | grep -q 200; do
  echo "   🔄 아직 $TARGET_COLOR 응답 없음... 기다리는 중..."
  sleep 1
  RETRY=$((RETRY+1))
  if [ "$RETRY" -ge "$MAX_RETRY" ]; then
    echo "❌ $TARGET_COLOR 서비스가 일정 시간 안에 응답하지 않음. 중단합니다."
    exit 1
  fi
done

echo "✅ Nginx now proxies to: $TARGET_COLOR"

# ======================
# Prometheus 설정 자동 생성
# ======================
if [ -x ./scripts/generate-prometheus-config.sh ]; then
  sudo bash ./scripts/generate-prometheus-config.sh
else
  echo "⚠️ Prometheus 설정 스크립트가 존재하지 않거나 실행 권한이 없습니다."
fi
