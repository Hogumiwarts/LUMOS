#!/bin/bash
set -e

COLOR=$1

if [[ "$COLOR" != "blue" && "$COLOR" != "green" ]]; then
  echo "❌ 사용법: ./cleanup.sh [blue|green]"
  exit 1
fi

sudo docker-compose \
  --project-name "$COLOR" \
  -f ./docker-compose.$COLOR.yml \
  down || true
