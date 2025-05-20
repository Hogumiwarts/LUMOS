#!/bin/bash
set -e

trap 'echo -e "\n❌ 에러 발생! 로그를 확인하세요."; read -p "Press enter to exit..."' ERR

print_step() {
  echo -e "\n\033[1;36m[STEP] $1\033[0m"
}

COLOR_FILE="scripts/dev/current_color.txt"

# 초기 상태 파일 없으면 blue로 시작
if [ ! -f "$COLOR_FILE" ]; then
  echo "blue" > "$COLOR_FILE"
fi

CURRENT_COLOR=$(cat "$COLOR_FILE")
NEXT_COLOR=$([[ "$CURRENT_COLOR" == "blue" ]] && echo "green" || echo "blue")

print_step "0. 공용 인프라(infra + ingress) 실행"
bash scripts/dev/test-infra.sh
sleep 2

print_step "1. 현재 운영 버전: $CURRENT_COLOR"
echo "📦 현재 버전은 [$CURRENT_COLOR]입니다. 새로운 버전을 [$NEXT_COLOR]에 배포합니다."
sleep 1

print_step "2. 새 버전 [$NEXT_COLOR]에 배포 및 전환"
bash scripts/dev/test-color.sh "$NEXT_COLOR"
sleep 2

print_step "3. 롤백 테스트 ($NEXT_COLOR → $CURRENT_COLOR)"
bash scripts/dev/rollback-local.sh
sleep 2

print_step "4. 이미지/컨테이너 정리"
echo "🧹 이미지/컨테이너 정리 중..."
docker image prune -f
docker container prune -f
echo "✅ 정리 완료!"

print_step "🎉 전체 테스트 완료! 브라우저에서 http://localhost 확인하세요"
