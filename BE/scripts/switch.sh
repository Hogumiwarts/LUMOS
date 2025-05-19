#!/bin/bash
set -e

COLOR_FILE="./scripts/current_color.txt"
NGINX_CONF="./nginx/nginx.conf"
NGINX_CONTAINER_NAME="lumos-nginx"

# current_color.txt 없으면 초기화
if [ ! -f "$COLOR_FILE" ]; then
  echo "blue" > "$COLOR_FILE"
fi

if [ ! -f "$NGINX_CONF" ]; then
  echo "❌ nginx.conf 파일이 없습니다: $NGINX_CONF"
  exit 1
fi

CURRENT_COLOR=$(cat "$COLOR_FILE")

# gateway 컨테이너 존재 여부 판단
gateway_exists() {
  sudo docker ps --format '{{.Names}}' | grep -q "$1"
}

if gateway_exists lumos-gateway-service-green; then
  TARGET_COLOR="green"
elif gateway_exists lumos-gateway-service-blue; then
  TARGET_COLOR="blue"
else
  echo "❌ gateway-service-blue/green 둘 다 없습니다. switch 중단"
  exit 1
fi

if [ "$CURRENT_COLOR" == "$TARGET_COLOR" ]; then
  echo "ℹ️ 현재 프록시 상태($CURRENT_COLOR)와 동일하므로 전환하지 않습니다."
  exit 0
fi

echo "▶ Switching from $CURRENT_COLOR to $TARGET_COLOR"

# OS 호환용 sed
if [[ "$OSTYPE" == "darwin"* ]]; then
  SED_INPLACE="sed -i ''"
else
  SED_INPLACE="sudo sed -i"
fi

if [ "$TARGET_COLOR" == "green" ]; then
  $SED_INPLACE 's/server lumos-gateway-service-blue:8080;/server lumos-gateway-service-green:8080;/' "$NGINX_CONF"
else
  $SED_INPLACE 's/server lumos-gateway-service-green:8080;/server lumos-gateway-service-blue:8080;/' "$NGINX_CONF"
fi

# nginx reload
if sudo docker ps --format '{{.Names}}' | grep -q "$NGINX_CONTAINER_NAME"; then
  echo "🔄 Reloading Nginx (in $NGINX_CONTAINER_NAME)"
  sudo docker exec "$NGINX_CONTAINER_NAME" nginx -s reload
else
  echo "❌ Nginx 컨테이너 $NGINX_CONTAINER_NAME 가 존재하지 않습니다."
  exit 1
fi

echo "$TARGET_COLOR" | sudo tee "$COLOR_FILE" > /dev/null

echo "⏳ 프록시 전환 후 $TARGET_COLOR 응답 대기 중..."
until curl -skL -L -o /dev/null -w "%{http_code}" http://localhost/ | grep -q 200
do
  echo "   🔄 아직 $TARGET_COLOR 응답 없음... 기다리는 중..."
  sleep 1
done

echo "✅ Nginx now proxies to: $TARGET_COLOR"

# Prometheus 설정 자동 생성
sudo bash ./scripts/generate-prometheus-config.sh
