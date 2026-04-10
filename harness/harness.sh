#!/bin/bash

# 경로 설정
HARNESS_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$HARNESS_DIR")"
STATE_FILE="$HARNESS_DIR/task_state.json"

cd "$PROJECT_ROOT"

# 도구 체크
for cmd in gemini jq claude; do
  if ! command -v $cmd &> /dev/null; then echo "❌ $cmd 가 설치되지 않았습니다."; exit 1; fi
done

# 1. 초기화 (사용자 입력이 있을 경우에만 실행)
if [ ! -z "$1" ]; then
  echo "🚀 초기 계획 수립 중..."
  # Gemini에게 명확한 JSON 구조를 강제합니다.
  gemini "사용자 요청: $1. 프로젝트 루트를 기준으로 @harness/task_state.json 형식에 맞춰 전체 계획과 첫 번째 명령을 작성해줘. JSON 외의 텍스트는 출력하지 마." > "$STATE_FILE"
fi

while true; do
  PHASE=$(jq -r '.phase' "$STATE_FILE")

  case $PHASE in
    "PLANNING"|"REPLANNING")
      echo "🤖 Gemini가 계획을 검토/수정 중입니다..."
      # 상태 파일을 읽어서 다음 단계를 결정하게 함
      gemini "$(cat "$STATE_FILE") 를 검토하고 다음 실행할 구체적인 코딩 명령을 'claude_instruction'에 넣어서 업데이트해줘. 완료되면 phase를 READY_FOR_CLAUDE로 바꿔." > "$STATE_FILE.tmp" && mv "$STATE_FILE.tmp" "$STATE_FILE"
      ;;

    "READY_FOR_CLAUDE")
      echo "👨‍💻 Claude 작업 시작..."
      INSTRUCTION=$(jq -r '.claude_instruction' "$STATE_FILE")

      # [핵심] android-executor 전용 에이전트를 비대화형(Print) 모드로 실행합니다.
      # --dangerously-skip-permissions: 파일 수정 시 매번 묻는 팝업을 스킵합니다.
      claude --agent android-executor \
             -p "harness/task_state.json을 읽고 claude_instruction을 수행해." \
             --dangerously-skip-permissions \
             --output-format json
      ;;

    "REVIEW_REQUIRED")
      echo "🧐 Gemini 리뷰 중..."
      # 현재 git diff와 실행 결과를 Gemini에게 전달
      DIFF=$(git diff)
      REVIEW_RESULT=$(gemini "상태: $(cat "$STATE_FILE") \n 코드변경사항: $DIFF \n 위 내용을 리뷰해줘. 통과면 phase를 COMPLETED로, 아니면 REPLANNING으로 바꾸고 이유를 feedback에 적어줘.")

      echo "$REVIEW_RESULT" > "$STATE_FILE"

      if [ "$(jq -r '.phase' "$STATE_FILE")" == "COMPLETED" ]; then
        echo "✅ 모든 작업이 검증을 통과했습니다!"
        exit 0
      fi
      ;;
  esac
  sleep 2
done