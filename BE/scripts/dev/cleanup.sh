#!/bin/bash
set -e

COLOR=$1

if [[ "$COLOR" != "blue" && "$COLOR" != "green" ]]; then
  echo "❌ 사용법: ./cleanup.sh [blue|green]"
  exit 1
fi

echo "🧹 $COLOR 앱 종료 중..."
docker-compose \
  --project-name "lumos-$COLOR" \
  -f "dev/docker-compose.$COLOR.dev.yml" \
  down || true
