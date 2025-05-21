#!/bin/bash
set -e  # 에러 발생 시 즉시 종료
set -x 	#실행되는 명령어 출력

# dev 전용 compose 파일과 env 파일
COMPOSE_FILE="dev/docker-compose.dev.yml"
ENV_FILE="dev/.env.dev"

echo "🛑 기존 dev 컨테이너 중지 및 삭제..."
docker-compose -f "$COMPOSE_FILE" down

echo "🚀 개발용 컨테이너 실행 중... (env: $ENV_FILE)"
docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" up --build -d

echo "✅ dev 환경 컨테이너가 성공적으로 실행되었습니다."
