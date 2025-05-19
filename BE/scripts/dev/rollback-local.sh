#!/bin/bash
set -e

trap 'echo -e "\n❌ 에러 발생! 롤백 실패. 로그를 확인하세요."; read -p "Press enter to exit..."' ERR

COLOR_FILE="scripts/dev/current_color.txt"
NGINX_CONF="dev/nginx/nginx.conf"
NGINX_CONTAINER_NAME="lumos-nginx"

if [ ! -f "$COLOR_FILE" ]; then
  echo "🚫 current_color.txt가 없습니다. 롤백할 수 없습니다."
  exit 1
fi

CURRENT_COLOR=$(cat "$COLOR_FILE")
PREV_COLOR=$([[ "$CURRENT_COLOR" == "blue" ]] && echo "green" || echo "blue")
GATEWAY_PORT=$([[ "$PREV_COLOR" == "blue" ]] && echo 8081 || echo 8082)
GATEWAY_CONTAINER_NAME="lumos-gateway-service-$PREV_COLOR"

echo -e "\n\033[1;36m[ROLLBACK] 현재: $CURRENT_COLOR → 이전: $PREV_COLOR\033[0m"

# 1. 이전 앱 실행
echo "▶ $PREV_COLOR 앱 실행..."
docker-compose \
  --project-name "lumos-$PREV_COLOR" \
  -f "dev/docker-compose.$PREV_COLOR.dev.yml" \
  up -d

# 2. gateway readiness 대기
echo "⏳ $PREV_COLOR gateway 준비 대기 중..."
until \
  curl -s "http://localhost:$GATEWAY_PORT/actuator/health" | grep '"status":"UP"' > /dev/null \
  && [ "$(curl -s -L -o /dev/null -w "%{http_code}" http://localhost:$GATEWAY_PORT/)" = "200" ]
do
  echo "⏳ 아직 gateway ($PREV_COLOR:$GATEWAY_PORT) 준비 안 됨..."
  sleep 1
done
echo "✅ gateway 준비 완료!"

# 3. nginx.conf 프록시 전환
if grep -q "server lumos-gateway-service-$CURRENT_COLOR:8080;" "$NGINX_CONF"; then
  echo "🔧 nginx.conf에서 gateway upstream 전환"

  if [[ "$OSTYPE" == "darwin"* ]]; then
    sed -i '' \
      "s/server lumos-gateway-service-$CURRENT_COLOR:8080;/server lumos-gateway-service-$PREV_COLOR:8080;/" "$NGINX_CONF"
  else
    sed -i \
      "s/server lumos-gateway-service-$CURRENT_COLOR:8080;/server lumos-gateway-service-$PREV_COLOR:8080;/" "$NGINX_CONF"
  fi
else
  echo "ℹ️ nginx.conf에는 이미 $PREV_COLOR가 설정되어 있습니다."
fi

# 4. nginx reload
if docker ps --format '{{.Names}}' | grep -q "$NGINX_CONTAINER_NAME"; then
  echo "🔄 Nginx Reload (in $NGINX_CONTAINER_NAME)"
  if ! docker exec "$NGINX_CONTAINER_NAME" nginx -s reload; then
    echo "⚠️ reload 실패 → nginx 컨테이너 재시작"
    docker restart "$NGINX_CONTAINER_NAME"
  fi
else
  echo "❌ Nginx 컨테이너가 존재하지 않습니다. 롤백 중단"
  exit 1
fi

# 5. 프록시 응답 대기
echo "⏳ 프록시 전환 반영 대기 중..."
until curl -s http://localhost/actuator/health | grep -q '"status":"UP"'; do
  echo "⏳ 프록시 대상 응답 없음... 기다리는 중..."
  sleep 1
done

# 6. 상태 파일 갱신
echo "$PREV_COLOR" > "$COLOR_FILE"

# 7. Prometheus 설정 regenerate
bash ./scripts/dev/generate-prometheus-config.sh

# 8. 이전 앱 종료 (현재는 PREV_COLOR가 운영 중이므로 CURRENT_COLOR를 종료)
bash scripts/dev/cleanup.sh "$CURRENT_COLOR"

echo "✅ 롤백 완료! 현재 운영 버전은 [$PREV_COLOR]입니다."
