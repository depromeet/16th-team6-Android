#!/bin/bash

# 경로 설정
HARNESS_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$HARNESS_DIR")"
STATE_FILE="$HARNESS_DIR/task_state.json"
ENV_FILE="$HARNESS_DIR/.env"
TEMP_FILE="$STATE_FILE.next"
BACKUP_FILE="$STATE_FILE.bak"
ERROR_LOG="$HARNESS_DIR/last_error_response.log"

# 색상 정의
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
RED='\033[0;31m'
NC='\033[0m'

cd "$PROJECT_ROOT" || { echo "Failed to cd to $PROJECT_ROOT"; exit 1; }

# 로그 출력 함수
log_info()    { echo -e "${BLUE}[INFO]${NC} $(date +'%H:%M:%S') $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $(date +'%H:%M:%S') $1"; }
log_warn()    { echo -e "${YELLOW}[WARN]${NC} $(date +'%H:%M:%S') $1"; }
log_error()   { echo -e "${RED}[ERROR]${NC} $(date +'%H:%M:%S') $1"; }

# .env 로드
if [ ! -f "$ENV_FILE" ]; then
  log_error ".env 파일이 없습니다: $ENV_FILE"
  exit 1
fi
# shellcheck source=/dev/null
source "$ENV_FILE"

# provider 기본값 설정
PLANNER_PROVIDER="${PLANNER_PROVIDER:-gemini}"
EXECUTOR_PROVIDER="${EXECUTOR_PROVIDER:-claude}"
REVIEWER_PROVIDER="${REVIEWER_PROVIDER:-gemini}"
EXECUTOR_CLAUDE_AGENT="${EXECUTOR_CLAUDE_AGENT:-android-executor}"

validate_provider() {
  local name="$1"
  local value="$2"

  case "$value" in
    gemini|claude|codex)
      ;;
    *)
      log_error "$name 값이 잘못되었습니다: $value"
      log_error "허용값: gemini | claude | codex"
      exit 1
      ;;
  esac
}

validate_provider "PLANNER_PROVIDER" "$PLANNER_PROVIDER"
validate_provider "EXECUTOR_PROVIDER" "$EXECUTOR_PROVIDER"
validate_provider "REVIEWER_PROVIDER" "$REVIEWER_PROVIDER"

# 사용 중인 provider 설치 여부 확인
check_required_tools() {
  local tools=("jq" "$PLANNER_PROVIDER" "$EXECUTOR_PROVIDER" "$REVIEWER_PROVIDER")
  local seen=()
  for cmd in "${tools[@]}"; do
    # 중복 제거
    [[ " ${seen[*]} " == *" $cmd "* ]] && continue
    seen+=("$cmd")
    if ! command -v "$cmd" &> /dev/null; then
      log_error "$cmd 가 설치되지 않았습니다."
      exit 1
    fi
  done
}

# 상태 표시 함수
print_status() {
  local phase goal feedback
  phase=$(jq -r '.phase // "NONE"' "$STATE_FILE" 2>/dev/null)
  goal=$(jq -r '.goal // "N/A"' "$STATE_FILE" 2>/dev/null)
  feedback=$(jq -r '.feedback // empty' "$STATE_FILE" 2>/dev/null)

  echo -e "\n${CYAN}━━━━━━━━━━━━━━━━━━━━━━━ TASK STATUS ━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  echo -e "${CYAN}▶ PHASE   :${NC} ${YELLOW}$phase${NC}"
  echo -e "${CYAN}▶ GOAL    :${NC} $goal"
  echo -e "${CYAN}▶ ENV     :${NC} planner=${YELLOW}$PLANNER_PROVIDER${NC}  executor=${YELLOW}$EXECUTOR_PROVIDER${NC}  reviewer=${YELLOW}$REVIEWER_PROVIDER${NC}"

  if [ -n "$feedback" ]; then
    echo -e "${RED}▶ FEEDBACK:${NC}\n${YELLOW}$feedback${NC}"
  fi

  echo -e "${CYAN}▶ STEPS   :${NC}"
  jq -c '.steps[]' "$STATE_FILE" 2>/dev/null | while read -r step; do
    local desc status
    desc=$(echo "$step" | jq -r '.description')
    status=$(echo "$step" | jq -r '.status')
    if [ "$status" == "DONE" ]; then
      echo -e "  ${GREEN}[DONE]${NC} $desc"
    elif [ "$status" == "IN_PROGRESS" ]; then
      echo -e "  ${YELLOW}[WAIT]${NC} $desc ${YELLOW}(작업 중...)${NC}"
    else
      echo -e "  ${NC}[TODO]${NC} $desc"
    fi
  done
  echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}\n"
}

# ── 텍스트 provider 호출 (planner / reviewer — JSON 응답 반환) ───────────────
call_text_provider() {
  local provider="$1"
  local prompt="$2"
  local result
  local exit_code
  local err_tmp

  err_tmp=$(mktemp)

  case "$provider" in
    gemini)
      result=$(echo -e "$prompt" | gemini 2> "$err_tmp")
      exit_code=$?
      ;;
    claude)
      result=$(claude -p "$prompt" 2> "$err_tmp")
      exit_code=$?
      ;;
    codex)
      # -a never 옵션이 에러를 유발하므로 제거하고 표준 입력 방식으로 호출합니다.
      result=$(echo "$prompt" | codex exec - --sandbox read-only 2> "$err_tmp")
      exit_code=$?
      ;;
    *)
      log_error "알 수 없는 provider: $provider"
      rm -f "$err_tmp"
      return 1
      ;;
  esac

  if [ $exit_code -ne 0 ]; then
    log_error "❌ $provider 호출 중 치명적 오류 발생 (Exit Code: $exit_code)"
    echo -e "\n--- [$(date +'%Y-%m-%d %H:%M:%S')] ERROR REPORT ---" >> "$ERROR_LOG"
    cat "$err_tmp" >> "$ERROR_LOG"
    echo "-------------------------------------------" >> "$ERROR_LOG"
    
    # 429 에러인 경우 더 구체적인 안내 출력
    if grep -qE "429|RESOURCE_EXHAUSTED|capacity" "$err_tmp"; then
      log_error "서버 용량이 부족하거나 할당량이 초과되었습니다. (429 Too Many Requests)"
    fi
    
    log_warn "상세 에러 리포트 확인: $ERROR_LOG"
    rm -f "$err_tmp"
    return $exit_code
  fi

  rm -f "$err_tmp"
  echo "$result"
}

# JSON 추출 및 검증 함수
save_state_safely() {
  local content="$1"
  local extracted

  if [ -z "$content" ]; then
    return 1
  fi

  [ -s "$STATE_FILE" ] && cp "$STATE_FILE" "$BACKUP_FILE"

  extracted=$(echo "$content" | sed -n '/^```json$/,/^```$/p' | sed '1d;$d')
  if [ -z "$extracted" ]; then
    extracted=$(echo "$content" | python3 -c "
import sys, re
text = sys.stdin.read()
m = re.search(r'\{[\s\S]*\}', text)
if m:
    print(m.group(0))
" 2>/dev/null)
  fi

  if [ -n "$extracted" ] && echo "$extracted" | jq '.' > "$TEMP_FILE" 2>/dev/null; then
    mv "$TEMP_FILE" "$STATE_FILE"
    rm -f "$BACKUP_FILE"
    return 0
  else
    # [HEURISTIC RECOVERY] JSON 추출 실패 시 텍스트에서 phase 유추 시도
    log_warn "JSON 추출 실패. 텍스트 응답에서 상태 유추를 시도합니다..."
    
    local inferred_phase=""
    if echo "$content" | grep -qiE "COMPLETED|SUCCESS|승인"; then
      inferred_phase="COMPLETED"
    elif echo "$content" | grep -qiE "REPLANNING|FAIL|반려|실패|조치|피드백"; then
      inferred_phase="REPLANNING"
    fi

    if [ -n "$inferred_phase" ]; then
      log_success "상태 유추 성공: $inferred_phase"
      local feedback_text
      feedback_text=$(echo "$content" | head -n 15 | tr '"' "'" | tr -d '\n')
      jq --arg phase "$inferred_phase" --arg feedback "$feedback_text" \
         '.phase = $phase | .feedback = $feedback' "$STATE_FILE" > "$TEMP_FILE"
      mv "$TEMP_FILE" "$STATE_FILE"
      rm -f "$BACKUP_FILE"
      return 0
    fi

    log_error "❌ 응답에서 유효한 JSON을 추출하거나 상태를 유추할 수 없습니다."
    echo -e "\n--- [$(date +'%Y-%m-%d %H:%M:%S')] INVALID JSON RESPONSE ---" >> "$ERROR_LOG"
    echo "$content" >> "$ERROR_LOG"
    echo "----------------------------------------------------" >> "$ERROR_LOG"
    log_warn "응답 내용을 확인하세요: $ERROR_LOG"
    rm -f "$TEMP_FILE"
    return 1
  fi
}

# ── executor provider 호출 (파일 변경 수행) ──────────────────────────────────
call_executor_provider() {
  case "$EXECUTOR_PROVIDER" in
    claude)
      (while true; do sleep 60; echo -ne "${YELLOW}.${NC}"; done) &
      local keep_alive_pid=$!
      claude --agent "$EXECUTOR_CLAUDE_AGENT" --verbose \
        -p "harness/task_state.json에 정의된 스텝을 수행하고 phase를 REVIEW_REQUIRED로 바꿔." \
        --dangerously-skip-permissions
      local exit_code=$?
      kill "$keep_alive_pid" 2>/dev/null
      echo -e ""
      return $exit_code
      ;;
    gemini)
      local instruction
      instruction=$(jq -r '.executor_instruction // "task_state.json에 정의된 스텝을 수행하고 phase를 REVIEW_REQUIRED로 바꿔."' "$STATE_FILE")
      local prompt="You are a coding agent. Execute every step in the following task state and update phase to REVIEW_REQUIRED.\n\nTASK STATE:\n$(cat "$STATE_FILE")\n\nINSTRUCTION:\n$instruction\n\nOutput ONLY valid JSON (updated task_state)."
      local result
      result=$(call_text_provider gemini "$prompt")
      save_state_safely "$result"
      ;;
    codex)
      local instruction
      instruction=$(jq -r '.executor_instruction // "task_state.json에 정의된 스텝을 수행하고 phase를 REVIEW_REQUIRED로 바꿔."' "$STATE_FILE")
      (while true; do sleep 60; echo -ne "${YELLOW}.${NC}"; done) &
      local keep_alive_pid=$!
      printf '%s\n\nTask state:\n%s\n' "$instruction" "$(cat "$STATE_FILE")" \
        | codex exec - --sandbox workspace-write -C "$PROJECT_ROOT" 2>&1
      local exit_code=$?
      kill "$keep_alive_pid" 2>/dev/null
      echo -e ""
      return $exit_code
      ;;

    *)
      log_error "[executor] 알 수 없는 provider: $EXECUTOR_PROVIDER"
      return 1
      ;;
  esac
}

# ── 도구 설치 확인 ────────────────────────────────────────────────────────────
check_required_tools

# ── 미션 초기화 ───────────────────────────────────────────────────────────────
if [ -n "$1" ]; then
  log_info "🚀 새로운 미션 접수: $1"
  jq -n --arg goal "$1" '{"phase": "PLANNING", "goal": $goal, "steps": [], "executor_instruction": ""}' > "$STATE_FILE"
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
    # ── PLANNER 담당 ────────────────────────────────────────────────────────
    "PLANNING"|"REPLANNING")
      step_count=$(jq '.steps | length' "$STATE_FILE" 2>/dev/null)
      feedback=$(jq -r '.feedback // empty' "$STATE_FILE" 2>/dev/null)

      if [ "$PHASE" == "REPLANNING" ] && [ -n "$feedback" ]; then
        log_warn "🔄 재계획(REPLANNING) 중입니다. 피드백 내용:"
        echo -e "${YELLOW}>> $feedback${NC}"
      fi

      if [ "$step_count" -gt 0 ] && [ "$PHASE" != "REPLANNING" ]; then
        log_info "💡 계획이 이미 존재합니다. READY_FOR_EXECUTE 전환을 기다립니다..."
        sleep 5
      else
        log_info "🤖 [$PLANNER_PROVIDER/planner] 계획 수립 중..."
        PROMPT="Use Planning Skill. You are a PM. Current state: $(cat "$STATE_FILE"). \n\nReview the goal and update 'steps' and 'executor_instruction'. Then set phase to 'READY_FOR_EXECUTE'. \n\nIMPORTANT: Output ONLY valid JSON."
        RESULT=$(call_text_provider "$PLANNER_PROVIDER" "$PROMPT") || exit 1
        save_state_safely "$RESULT" || exit 1
      fi
      ;;

    # ── EXECUTOR 담당 ───────────────────────────────────────────────────────
    # READY_FOR_CLAUDE는 하위 호환성을 위해 READY_FOR_EXECUTE와 동일하게 처리
    "READY_FOR_EXECUTE"|"READY_FOR_CLAUDE")
      log_info "👨‍💻 [$EXECUTOR_PROVIDER/executor] 작업 시작..."
      call_executor_provider
      log_info "⏳ executor 작업 완료."
      sleep 3
      ;;

    # ── REVIEWER 담당 ───────────────────────────────────────────────────────
    "REVIEW_REQUIRED")
      log_info "🧐 [$REVIEWER_PROVIDER/reviewer] 변경 사항 검증 중..."

      log_info "🔨 빌드 검증을 시작합니다..."
      BUILD_LOG=$(./gradlew assembleDebug 2>&1 | tee /dev/stderr)
      BUILD_EXIT=${PIPESTATUS[0]}

      DIFF_STAT=$(git diff --stat)
      DIFF_FULL=$(git diff)

      echo -e "${YELLOW}--- [CHANGE SUMMARY] ---${NC}"
      echo "$DIFF_STAT"

      if [ $BUILD_EXIT -ne 0 ]; then
        log_error "❌ 빌드 실패! 오류 내용을 분석하여 피드백을 전달합니다."
        PROMPT="You are a Senior Architect. The build FAILED after the executor's changes. \n\n[BUILD ERROR]\n$BUILD_LOG \n\n[DIFF]\n$DIFF_FULL \n\nAnalyze the error and set phase to 'REPLANNING' with a specific 'feedback'. Output ONLY valid JSON, NO other text."
      else
        log_success "✅ 빌드 성공! 코드 리뷰를 진행합니다."
        PROMPT="You are a Senior Architect. The build was SUCCESSFUL. Review the following changes based on the goal: $(jq -r '.goal' "$STATE_FILE")\n\n[DIFF SUMMARY]\n$DIFF_STAT\n\n[FULL DIFF]\n$DIFF_FULL\n\nIf the code quality is good, set phase to 'COMPLETED'. If not, set to 'REPLANNING' with feedback. Output ONLY valid JSON, NO other text."
      fi

      RESULT=$(call_text_provider "$REVIEWER_PROVIDER" "$PROMPT") || exit 1
      if save_state_safely "$RESULT"; then
        if [ "$(jq -r '.phase' "$STATE_FILE")" == "COMPLETED" ]; then
          log_success "🎉 모든 작업이 승인되었습니다!"
          studio . &
          exit 0
        fi
      else
        exit 1
      fi
      ;;

    "COMPLETED")
      log_success "작업이 이미 완료되었습니다."
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
