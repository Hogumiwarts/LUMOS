#!/bin/bash
set -e

# ========================
# 설정
# ========================
COLOR_FILE="./scripts/current_color.txt"
NGINX_CONF="./nginx/nginx.conf"
NGINX_CONTAINER_NAME="lumos-nginx"

if [ ! -f "$COLOR_FILE" ]; then
  echo "🚫 current_color.txt가 없습니다. 롤백할 수 없습니다."
  exit 1
fi

CURRENT_COLOR=$(cat "$COLOR_FILE")
PREV_COLOR=$([ "$CURRENT_COLOR" == "blue" ] && echo "green" || echo "blue")
PORT=$([ "$PREV_COLOR" == "blue" ] && echo 8081 || echo 8082)

echo "⏪ 롤백 시작: 현재 $CURRENT_COLOR → 이전 $PREV_COLOR"

# ========================
# 1. 이전 앱 재실행
# ========================
echo "▶ $PREV_COLOR 앱 실행..."
sudo docker-compose \
  --project-name "$PREV_COLOR" \
  -f ./docker-compose.$PREV_COLOR.yml \
  --env-file .env.prod up -d --no-recreate

# ========================
# 2. readiness 체크
# ========================
echo "⏳ [WAIT] gateway 완전 기동 대기 중..."

until curl -skL http://localhost:$PORT/actuator/health | grep '"status":"UP"' > /dev/null && \
      [ "$(curl -skL -L -o /dev/null -w "%{http_code}" http://localhost:$PORT)" = "200" ]; do
  echo "   🔄 아직 gateway ($PREV_COLOR:$PORT) 준비 안 됨..."
  sleep 1
done

echo "✅ Gateway ($PREV_COLOR) 완전 기동 완료!"

# ========================
# 3. nginx.conf 전환
# ========================
if grep -q "server lumos-gateway-service-$CURRENT_COLOR:8080;" "$NGINX_CONF"; then
  echo "🔧 nginx.conf에서 gateway upstream 전환"
  sudo sed -i "s/server lumos-gateway-service-$CURRENT_COLOR:8080;/server lumos-gateway-service-$PREV_COLOR:8080;/" "$NGINX_CONF"
else
  echo "ℹ️ nginx.conf에는 이미 $PREV_COLOR가 설정되어 있습니다."
fi

# ========================
# 4. Nginx Reload
# ========================
if sudo docker ps --format '{{.Names}}' | grep -q "$NGINX_CONTAINER_NAME"; then
  echo "🔄 Nginx Reload (in $NGINX_CONTAINER_NAME)"
  if ! sudo docker exec "$NGINX_CONTAINER_NAME" nginx -s reload; then
    echo "⚠️ reload 실패 → nginx 컨테이너 재시작"
    sudo docker restart "$NGINX_CONTAINER_NAME"
  fi
else
  echo "❌ Nginx 컨테이너가 존재하지 않습니다. 롤백 중단"
  exit 1
fi

# ========================
# 5. 프록시 반영 확인
# ========================
echo "⏳ 프록시 전환 반영 대기 중..."

until curl -skL http://localhost/actuator/health | grep -q '"status":"UP"'; do
  echo "   🔄 프록시 대상 응답 없음... 기다리는 중..."
  sleep 1
done

# ========================
# 6. 상태 기록 갱신
# ========================
echo "$PREV_COLOR" | sudo tee $COLOR_FILE > /dev/null

# ========================
# 7. 현재 앱 종료
# ========================
echo "🧹 $CURRENT_COLOR 앱 종료 중..."
bash ./scripts/cleanup.sh "$CURRENT_COLOR"

# Prometheus 설정 자동 생성
sudo bash ./scripts/generate-prometheus-config.sh

echo "✅ 롤백 완료: 현재 운영은 $PREV_COLOR 입니다."
