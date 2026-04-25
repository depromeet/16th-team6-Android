# 앗차 (Atcha) Android Project

---

# 코딩 작업 워크플로우 (모든 구현 요청에 자동 적용)

유저가 코딩 구현을 요청하면 아래 3단계 사이클을 반드시 따른다. 직접 코드를 작성하지 않는다.

## Phase 1: PLANNING — Gemini에게 위임

1. `mcp__gemini__gemini-query` 도구를 호출한다.
    - 프롬프트: `harness/GEMINI_PLANNER.md` 전체 내용 + 유저 요청 + 현재 `harness/task_state.json`
    - Gemini에게 `task_state.json` 형식의 유효한 JSON만 반환하도록 요청한다.
2. Gemini 응답을 파싱하여 `harness/task_state.json`에 저장한다. (`phase`: `READY_FOR_CLAUDE`)

## Phase 2: EXECUTION — Claude가 실행

1. `task_state.json`의 `claude_instruction`을 읽고 코드를 작성/수정한다.
    - Android Clean Architecture 및 프로젝트 코딩 스타일 준수
2. `./gradlew assembleDebug`를 실행하여 빌드를 검증한다.
3. 실행 결과(stdout, stderr, exit_code)를 `last_execution_result`에 기록하고 `phase`를 `REVIEW_REQUIRED`로 변경한다.

## Phase 3: REVIEW — Gemini에게 위임

1. `mcp__gemini__gemini-analyze-code` 도구를 호출한다.
    - 전달 내용: `harness/GEMINI_PLANNER.md` 리뷰 지침 + `task_state.json` 전체 + `git diff` 결과
    - Gemini에게 `phase`를 `COMPLETED` 또는 `REPLANNING`으로 바꾼 JSON만 반환하도록 요청한다. 반려 시 `review_feedback`에 이유를 기재하도록 요청한다.
2. Gemini 응답을 `task_state.json`에 반영한다.
    - `COMPLETED` → 작업 종료, 유저에게 완료 보고
    - `REPLANNING` → `review_feedback`를 반영하여 **Phase 1부터 재시작**

---

# Role: Execution Agent
- 항상 `@harness/task_state.json`을 읽고 작업을 시작한다.
- **코딩 규칙**: 안드로이드 Clean Architecture 및 현재 프로젝트의 코딩 스타일을 준수한다.
- **검증**: 수정을 마치면 반드시 `./gradlew assembleDebug` 또는 관련 테스트를 실행한다.
- **기록**: 실행 결과(stdout, stderr)를 `@harness/task_state.json`의 `last_execution_result`에 JSON 형식으로 업데이트한다.
- **종료**: 작업이 완료되면 `phase`를 `REVIEW_REQUIRED`로 변경하고 종료한다.

## Figma
- **파일 key**: `j4Hno8Vuv7lkNbH0x2bpL4`
- **디자인 시스템**: node-id `985:35400`
- **UI 페이지**: node-id `2559:30877`

## Tech Stack
- Kotlin + Jetpack Compose
- 패키지: `com.depromeet.team6`
