#!/bin/bash

# 경로 설정
HARNESS_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$HARNESS_DIR")"
STATE_FILE="$HARNESS_DIR/task_state.json"
TEMP_FILE="$STATE_FILE.next"

# 색상 정의
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
RED='\033[0;31m'
NC='\033[0m' # No Color

cd "$PROJECT_ROOT"

# 로그 출력 함수
log_info() { echo -e "${BLUE}[INFO]${NC} $(date +'%H:%M:%S') $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $(date +'%H:%M:%S') $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $(date +'%H:%M:%S') $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $(date +'%H:%M:%S') $1"; }

# 상태 표시 함수
print_status() {
  local phase=$(jq -r '.phase // "NONE"' "$STATE_FILE")
  local goal=$(jq -r '.goal // "N/A"' "$STATE_FILE")
  
  echo -e "\n${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  echo -e "${CYAN}▶ CURRENT PHASE:${NC} ${YELLOW}$phase${NC}"
  echo -e "${CYAN}▶ GOAL:${NC} $goal"
  
  # 진행 중인 스텝 표시
  if [ "$phase" == "READY_FOR_CLAUDE" ]; then
    echo -e "${CYAN}▶ PROGRESS:${NC}"
    jq -c '.steps[]' "$STATE_FILE" | while read -r step; do
      local desc=$(echo "$step" | jq -r '.description')
      local status=$(echo "$step" | jq -r '.status')
      if [ "$status" == "DONE" ]; then
        echo -e "  ${GREEN}✓${NC} $desc"
      elif [ "$status" == "IN_PROGRESS" ]; then
        echo -e "  ${YELLOW}▶${NC} $desc ${YELLOW}(작업 중...)${NC}"
      else
        echo -e "  ${NC}○${NC} $desc"
      fi
    done
  fi
  echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}\n"
}

# JSON 추출 및 검증 함수
save_state_safely() {
  local content="$1"
  local extracted
  
  extracted=$(echo "$content" | sed -n '/^```json$/,/^```$/p' | sed '1d;$d')
  if [ -z "$extracted" ]; then extracted=$(echo "$content" | sed -n '/^{/,/}$/p'); fi
  if [ -z "$extracted" ]; then extracted="$content"; fi

  if echo "$extracted" | jq '.' > "$TEMP_FILE" 2>/dev/null; then
    mv "$TEMP_FILE" "$STATE_FILE"
    return 0
  else
    log_error "유효하지 않은 JSON 데이터가 수신되었습니다."
    rm -f "$TEMP_FILE"
    return 1
  fi
}

# 도구 체크
for cmd in gemini jq claude; do
  if ! command -v $cmd &> /dev/null; then log_error "$cmd 가 설치되지 않았습니다."; exit 1; fi
done

# 1. 초기화
if [ ! -z "$1" ]; then
  log_info "🚀 새로운 미션 접수: $1"
  log_info "🤖 Gemini가 초기 계획을 수립 중입니다..."
  RESULT=$(gemini "사용자 요청: $1. 프로젝트 루트를 기준으로 @harness/task_state.json 형식에 맞춰 전체 계획과 첫 번째 명령을 작성해줘. JSON 외의 텍스트는 출력하지 마.")
  save_state_safely "$RESULT"
fi

while true; do
  if [ ! -s "$STATE_FILE" ]; then
    log_warn "상태 파일 대기 중..."
    sleep 5
    continue
  fi

  PHASE=$(jq -r '.phase // "NONE"' "$STATE_FILE")
  print_status

  case $PHASE in
    "PLANNING"|"REPLANNING")
      log_info "🤖 Gemini가 계획을 검토하고 있습니다..."
      RESULT=$(gemini "$(cat "$STATE_FILE") 를 검토하고 다음 실행할 구체적인 코딩 명령을 'claude_instruction'에 넣어서 업데이트해줘. 완료되면 phase를 READY_FOR_CLAUDE로 바꿔.")
      save_state_safely "$RESULT"
      ;;

    "READY_FOR_CLAUDE")
      log_info "👨‍💻 Claude가 작업을 시작합니다. (작업 내용을 실시간으로 업데이트합니다)"
      # Claude에게 작업을 맡기고, 백그라운드에서 상태를 실시간 모니터링할 수 있도록 유도
      claude --agent android-executor \
             -p "harness/task_state.json을 읽고 작업을 수행해. 각 스텝 시작 시 status를 IN_PROGRESS로, 완료 시 DONE으로 바꾸면서 작업해. 최종 완료 후 phase를 REVIEW_REQUIRED로 업데이트해." \
             --dangerously-skip-permissions
      
      log_info "⏳ Claude 작업 종료 대기 중..."
      sleep 5
      ;;

    "REVIEW_REQUIRED")
      log_info "🧐 Gemini가 코드 변경 사항을 리뷰하고 빌드 결과를 확인 중입니다..."
      DIFF=$(git diff)
      RESULT=$(gemini "현재 상태: $(cat "$STATE_FILE") \n 코드 변경 사항: $DIFF \n 위 내용을 리뷰해줘. 통과면 phase를 COMPLETED로, 아니면 REPLANNING으로 바꾸고 이유를 feedback에 적어줘.")
      save_state_safely "$RESULT"

      if [ "$(jq -r '.phase' "$STATE_FILE")" == "COMPLETED" ]; then
        log_success "축하합니다! 모든 작업이 검증을 통과했습니다. 🎉"
        exit 0
      fi
      ;;

    "COMPLETED")
      log_success "작업이 완료된 상태입니다."
      exit 0
      ;;

    *)
      sleep 5
      ;;
  esac
  sleep 2
done