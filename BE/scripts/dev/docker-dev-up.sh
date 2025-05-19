#!/bin/bash

set -e

trap 'echo -e "\n❌ 에러 발생! 로그를 확인하세요."; read -p "Press enter to exit..."' ERR

# Compose 파일 정의
COMPOSE_INFRA="dev/docker-compose.infrastructure.dev.yml"
COMPOSE_INGRESS="dev/docker-compose.ingress.dev.yml"
COMPOSE_APP="dev/docker-compose.blue.dev.yml"
ENV_FILE="dev/.env.dev"

echo "🛑 기존 앱 서비스 중지 및 삭제..."
docker-compose -f "$COMPOSE_APP" down

echo "🚀 dev 환경 컨테이너 실행 중..."
docker-compose \
  -f "$COMPOSE_INFRA" \
  -f "$COMPOSE_INGRESS" \
  -f "$COMPOSE_APP" \
  --env-file "$ENV_FILE" \
  up --build -d

# gateway 포트 확인 (dev는 보통 8080)
GATEWAY_PORT=8081

echo "⏳ Gateway 준비 대기 중... (localhost:$GATEWAY_PORT)"
until curl -s "http://localhost:$GATEWAY_PORT/actuator/health" | grep -q '"status":"UP"'; do
  echo "   🔄 아직 gateway 응답 없음... 기다리는 중..."
  sleep 1
done

echo "✅ Gateway 완전 기동 완료!"
echo "✅ dev 환경이 정상적으로 실행되었습니다."
