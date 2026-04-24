#!/bin/bash

# 경로 설정
HARNESS_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$HARNESS_DIR")"
STATE_FILE="$HARNESS_DIR/task_state.json"
TEMP_FILE="$STATE_FILE.next"
BACKUP_FILE="$STATE_FILE.bak"
ERROR_LOG="$HARNESS_DIR/last_error_response.log"

# 색상 정의
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
RED='\033[0;31m'
NC='\033[0m' # No Color

cd "$PROJECT_ROOT" || { echo "Failed to cd to $PROJECT_ROOT"; exit 1; }

# 로그 출력 함수
log_info()    { echo -e "${BLUE}[INFO]${NC} $(date +'%H:%M:%S') $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $(date +'%H:%M:%S') $1"; }
log_warn()    { echo -e "${YELLOW}[WARN]${NC} $(date +'%H:%M:%S') $1"; }
log_error()   { echo -e "${RED}[ERROR]${NC} $(date +'%H:%M:%S') $1"; }

# 상태 표시 함수
print_status() {
  local phase=$(jq -r '.phase // "NONE"' "$STATE_FILE" 2>/dev/null)
  local goal=$(jq -r '.goal // "N/A"' "$STATE_FILE" 2>/dev/null)

  echo -e "\n${CYAN}━━━━━━━━━━━━━━━━━━━━━━━ TASK STATUS ━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  echo -e "${CYAN}▶ PHASE:${NC} ${YELLOW}$phase${NC}"
  echo -e "${CYAN}▶ GOAL :${NC} $goal"
  echo -e "${CYAN}▶ STEPS:${NC}"

  jq -c '.steps[]' "$STATE_FILE" 2>/dev/null | while read -r step; do
    local desc=$(echo "$step" | jq -r '.description')
    local status=$(echo "$step" | jq -r '.status')
    if [ "$status" == "DONE" ]; then
      echo -e "  ${GREEN} [DONE] ${NC} $desc"
    elif [ "$status" == "IN_PROGRESS" ]; then
      echo -e "  ${YELLOW} [WAIT] ${NC} $desc ${YELLOW}(Claude 작업 중...)${NC}"
    else
      echo -e "  ${NC} [TODO] ${NC} $desc"
    fi
  done
  echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}\n"
}

# JSON 추출 및 검증 함수
save_state_safely() {
  local content="$1"
  local extracted

  # 백업 보존
  [ -s "$STATE_FILE" ] && cp "$STATE_FILE" "$BACKUP_FILE"

  # JSON 블록 추출 시도 (Markdown ```json ... ```)
  extracted=$(echo "$content" | sed -n '/^```json$/,/^```$/p' | sed '1d;$d')
  
  # 정규식을 이용한 중괄호 추출 (Python)
  if [ -z "$extracted" ]; then
    extracted=$(echo "$content" | python3 -c "
import sys, re
text = sys.stdin.read()
m = re.search(r'\{[\s\S]*\}', text)
if m:
    print(m.group(0))
" 2>/dev/null)
  fi

  # 최종 검증 및 저장
  if [ ! -z "$extracted" ] && echo "$extracted" | jq '.' > "$TEMP_FILE" 2>/dev/null; then
    mv "$TEMP_FILE" "$STATE_FILE"
    rm -f "$BACKUP_FILE"
    return 0
  else
    log_error "유효하지 않은 JSON 응답입니다. 상세 내용을 $ERROR_LOG 에 기록했습니다."
    echo "--- RAW CONTENT START ---" > "$ERROR_LOG"
    echo "$content" >> "$ERROR_LOG"
    echo "--- RAW CONTENT END ---" >> "$ERROR_LOG"
    rm -f "$TEMP_FILE"
    return 1
  fi
}

# Gemini 호출 래퍼 (stdin 사용)
call_gemini() {
  local prompt="$1"
  local result
  
  result=$(echo -e "$prompt" | gemini 2>&1)
  local exit_code=$?
  
  if [ $exit_code -ne 0 ]; then
    log_error "Gemini 호출 실패 (Exit Code: $exit_code)"
    return 1
  fi
  echo "$result"
}

# 도구 체크
for cmd in gemini jq claude; do
  if ! command -v $cmd &> /dev/null; then log_error "$cmd 가 설치되지 않았습니다."; exit 1; fi
done

# 1. 미션 초기화
if [ ! -z "$1" ]; then
  log_info "🚀 새로운 미션 접수: $1"
  echo "{\"phase\": \"PLANNING\", \"goal\": \"$1\", \"steps\": [], \"claude_instruction\": \"\"}" > "$STATE_FILE"
  log_info "🤖 초기 상태가 설정되었습니다. PLANNING 단계로 진입합니다."
fi

if [ ! -s "$STATE_FILE" ]; then
  log_error "task_state.json이 비어있습니다."
  exit 1
fi

FAIL_COUNT=0
MAX_FAILS=3
LAST_PHASE=""

while true; do
  PHASE=$(jq -r '.phase // "NONE"' "$STATE_FILE" 2>/dev/null)
  print_status

  if [ "$PHASE" == "$LAST_PHASE" ]; then
    FAIL_COUNT=$((FAIL_COUNT + 1))
    if [ $FAIL_COUNT -ge $MAX_FAILS ]; then
      log_error "Phase '$PHASE'에서 ${MAX_FAILS}회 연속 실패로 루프를 종료합니다."
      exit 1
    fi
  else
    FAIL_COUNT=0
    LAST_PHASE="$PHASE"
  fi

  case $PHASE in
    "PLANNING"|"REPLANNING")
      step_count=$(jq '.steps | length' "$STATE_FILE" 2>/dev/null)
      if [ "$step_count" -gt 0 ] && [ "$PHASE" != "REPLANNING" ]; then
        log_info "💡 지휘관의 계획을 감지했습니다. READY_FOR_CLAUDE 전환을 기다립니다..."
        sleep 5
      else
        log_info "🤖 Gemini가 계획을 수립 중입니다..."
        PROMPT="You are a PM. Current state: $(cat "$STATE_FILE"). \n\nReview the goal and update 'steps' and 'claude_instruction'. Then set phase to 'READY_FOR_CLAUDE'. \n\nIMPORTANT: Output ONLY valid JSON."
        RESULT=$(call_gemini "$PROMPT")
        save_state_safely "$RESULT"
      fi
      ;;

    "READY_FOR_CLAUDE")
      log_info "👨‍💻 Claude 작업 시작..."
      claude --agent android-executor -p "harness/task_state.json에 정의된 스텝을 수행하고 phase를 REVIEW_REQUIRED로 바꿔." --dangerously-skip-permissions
      log_info "⏳ Claude 작업 완료."
      sleep 3
      ;;
    "REVIEW_REQUIRED")
      log_info "🧐 Gemini가 변경 사항을 검증하고 리뷰 중입니다..."

      # 빌드 검증 (review-and-build 스킬의 핵심 로직 반영)
      log_info "🔨 빌드 검증을 시작합니다..."
      BUILD_LOG=$(./gradlew assembleDebug --quiet 2>&1)
      BUILD_EXIT=$?

      DIFF_STAT=$(git diff --stat)
      DIFF_FULL=$(git diff)

      echo -e "${YELLOW}--- [CHANGE SUMMARY] ---${NC}"
      echo "$DIFF_STAT"

      if [ $BUILD_EXIT -ne 0 ]; then
        log_error "❌ 빌드 실패! 오류 내용을 분석하여 Claude에게 피드백을 전달합니다."
        PROMPT="You are a Senior Architect. The build FAILED after Claude's changes. \n\n[BUILD ERROR]\n$BUILD_LOG \n\n[DIFF]\n$DIFF_FULL \n\nAnalyze the error and set phase to 'REPLANNING' with a specific 'feedback' for Claude to fix it. Output ONLY valid JSON."
      else
        log_success "✅ 빌드 성공! 코드 리뷰를 진행합니다."
        PROMPT="You are a Senior Architect. The build was SUCCESSFUL. Review the following changes based on the goal: $(jq -r '.goal' "$STATE_FILE")\n\n[DIFF SUMMARY]\n$DIFF_STAT\n\n[FULL DIFF]\n$DIFF_FULL\n\nIf the code quality is good, set phase to 'COMPLETED'. If not, set to 'REPLANNING' with feedback. Output ONLY valid JSON."
      fi

      RESULT=$(call_gemini "$PROMPT")
      if save_state_safely "$RESULT"; then
        if [ "$(jq -r '.phase' "$STATE_FILE")" == "COMPLETED" ]; then
          log_success "🎉 모든 작업이 승인되었습니다! 안드로이드 스튜디오를 엽니다."
          studio . &
          exit 0
        fi
      fi
      ;;

    "COMPLETED")
      log_success "작업이 이미 완료되었습니다. 안드로이드 스튜디오를 엽니다."
      studio . &
      exit 0
      ;;

    *)
      log_warn "알 수 없는 상태 '$PHASE'. 5초 후 재시도..."
      sleep 5
      ;;
  esac
  sleep 2
done
