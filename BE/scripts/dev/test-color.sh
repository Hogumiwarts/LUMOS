#!/bin/bash
set -e

COLOR=$1

if [[ "$COLOR" != "blue" && "$COLOR" != "green" ]]; then
  echo "❌ 사용법: ./test-color.sh [blue|green]"
  exit 1
fi

COLOR_FILE="scripts/dev/current_color.txt"

# app-network는 처음 한 번만 생성
echo "🧱 [1] app-network 생성 (이미 있으면 생략)"
docker network inspect app-network > /dev/null 2>&1 || docker network create app-network

# 서비스 실행
echo "📦 [2] $COLOR 앱 실행"
docker-compose \
  --project-name "lumos-$COLOR" \
  -f "dev/docker-compose.$COLOR.dev.yml" \
  up -d

# 포트 지정
if [ "$COLOR" == "blue" ]; then
  GATEWAY_PORT=8081
else
  GATEWAY_PORT=8082
fi

# health check + readiness
echo "⏳ [3] $COLOR gateway 준비 대기 중..."
until \
  curl -s "http://localhost:$GATEWAY_PORT/actuator/health" | grep '"status":"UP"' > /dev/null \
  && [ "$(curl -s -L -o /dev/null -w "%{http_code}" http://localhost:$GATEWAY_PORT/)" = "200" ]
do
  echo "⏳ gateway 아직 완전히 준비 안됨..."
  sleep 1
done

# 프록시 전환
echo "🔄 [4] switch.sh로 Nginx 프록시 전환"
bash scripts/dev/switch-local.sh

echo "✅ 전환 완료: http://localhost 에서 $COLOR 서비스 확인 가능"
echo "$COLOR" > "$COLOR_FILE"
