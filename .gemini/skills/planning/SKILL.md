---
name: planning
description: 기능을 분석하여 harness/harness.sh를 통해 작업을 위임하고 전체 프로세스를 관리합니다.
---
# Instructions

이 스킬은 사용자가 기능 구현이나 코드 수정을 요청했을 때, `harness/harness.sh`를 활용하여 Claude Code에게 작업을 위임하고 전체 진행 상태를 관리하도록 합니다.

## Workflow

### 1. Research & Analysis
Gemini는 다음 도구들을 사용하여 구현에 필요한 컨텍스트를 수집해야 합니다.
- **파일 참조**: `codebase_investigator`를 사용하여 수정 또는 참조가 필요한 핵심 파일들을 식별합니다.
- **아키텍처**: 프로젝트 내의 디자인 패턴과 스타일 가이드를 파악합니다.

### 2. Harness Execution
분석이 완료되면 `run_shell_command`를 사용하여 `harness/harness.sh`를 실행합니다.
- 명령어 형식: `bash harness/harness.sh "<사용자 요청 및 분석된 컨텍스트>"`
- 이 스크립트는 `task_state.json`을 생성하고 Claude Code를 호출하여 작업을 수행합니다.

### 3. Monitoring & Review
- `harness.sh`가 실행되는 동안 상태를 모니터링합니다.
- Claude의 작업이 완료되면(`REVIEW_REQUIRED` 상태), 코드 변경 사항을 검토하고 `review-and-build` 스킬을 사용하여 검증합니다.

## Constraints
- Gemini는 직접 파일을 수정하지 않습니다.
- 모든 작업 위임은 `harness/harness.sh`를 통해 이루어져야 합니다.
- `harness.sh` 실행 시 전달하는 인자에 분석된 핵심 파일 경로와 설계 지침을 포함시켜 Claude가 정확히 작업할 수 있도록 돕습니다.

## 프롬프트 예시 (Compact English)                                                                                                                                     │
- "Define User DTO: id, name, email. Add Gson annotations."                                                                                                            │
- "Add biometric auth to LoginScreen.kt using Credentials API."                                                                                                        │
- "Fix memory leak in LocationService.kt. Unregister listener in onDestroy."                                                                                           │
