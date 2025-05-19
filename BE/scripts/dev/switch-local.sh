#!/bin/bash

set -e

# =============================
# switch-local.sh
# =============================

COLOR_FILE="scripts/dev/current_color.txt"
NGINX_CONF="dev/nginx/nginx.conf"
NGINX_CONTAINER_NAME="lumos-nginx"

if [ ! -f "$COLOR_FILE" ]; then
  echo "blue" > "$COLOR_FILE"
fi

CURRENT_COLOR=$(cat "$COLOR_FILE")

gateway_exists() {
  docker ps --format '{{.Names}}' | grep -q "$1"
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

if [[ "$OSTYPE" == "darwin"* ]]; then
  SED_INPLACE="sed -i ''"
else
  SED_INPLACE="sed -i"
fi

if [ "$TARGET_COLOR" == "green" ]; then
  $SED_INPLACE 's/server lumos-gateway-service-blue:8080;/server lumos-gateway-service-green:8080;/' "$NGINX_CONF"
else
  $SED_INPLACE 's/server lumos-gateway-service-green:8080;/server lumos-gateway-service-blue:8080;/' "$NGINX_CONF"
fi

if docker ps --format '{{.Names}}' | grep -q "$NGINX_CONTAINER_NAME"; then
  echo "🔄 Reloading Nginx (in $NGINX_CONTAINER_NAME)"
  docker exec "$NGINX_CONTAINER_NAME" nginx -s reload
else
  echo "❌ Nginx 컨테이너 $NGINX_CONTAINER_NAME 가 존재하지 않습니다."
  exit 1
fi

echo "$TARGET_COLOR" > "$COLOR_FILE"
echo "✅ Nginx now proxies to: $TARGET_COLOR"

echo "⏳ 프록시 전환 후 green 응답 대기 중..."
until curl -s -L -o /dev/null -w "%{http_code}" http://localhost/ | grep -q 200
do
  echo "   🔄 아직 green이 응답하지 않음... 기다리는 중..."
  sleep 1
done

echo "✅ Nginx 프록시 전환 완전히 반영됨"
bash ./scripts/dev/generate-prometheus-config.sh
