#!/bin/bash
set -e

# 빌드 대상 서비스 디렉토리 목록
services=(
  "gateway-service"
  "auth-service"
  "device-service"
  "gesture-sensor-service"
  "gesture-service"
  "member-service"
  "routine-service"
)

# 각 서비스 디렉토리에서 빌드 실행
for service in "${services[@]}"
do
  service_path="../../$service"
  echo "🔨 Building $service..."

  if [ -f "$service_path/gradlew" ]; then
    (cd "$service_path" && ./gradlew clean build -x check)
  else
    echo "⚠️  $service_path/ 에 gradlew 파일이 없습니다. 건너뜁니다."
  fi

  echo ""
done

echo "✅ 모든 서비스 빌드 완료"
