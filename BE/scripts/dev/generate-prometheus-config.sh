#!/bin/bash

set -e

COLOR_FILE="./scripts/dev/current_color.txt"
TEMPLATE_FILE="./dev/prometheus/prometheus.template.yml"
OUTPUT_FILE="./dev/prometheus/prometheus.yml"

if [ ! -f "$COLOR_FILE" ]; then
  echo "🚫 색상 파일이 없습니다: $COLOR_FILE"
  exit 1
fi

CURRENT_COLOR=$(cat "$COLOR_FILE")

echo "🎯 현재 운영 중인 색상: $CURRENT_COLOR"

if [ ! -f "$TEMPLATE_FILE" ]; then
  echo "🚫 템플릿 파일이 없습니다: $TEMPLATE_FILE"
  exit 1
fi

# 템플릿의 {{color}} 부분을 현재 색상으로 치환하여 prometheus.yml 생성
sed "s/{{color}}/$CURRENT_COLOR/g" "$TEMPLATE_FILE" > "$OUTPUT_FILE"

echo "✅ Prometheus 설정 파일 생성 완료: $OUTPUT_FILE"

# Prometheus 컨테이너 재시작 (선택)
echo "🔄 Prometheus 컨테이너 재시작: lumos-prometheus"
docker restart lumos-prometheus
