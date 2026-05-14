---
name: planning
description: harness.sh 가 실행되는 도중 task_state 의 phase 가 PLANNING 단계일 때 활성화 됩니다.
---
# Instructions
이 스킬은 사용자가 기능 구현이나 코드 수정을 요청했을 때, `harness/harness.sh`를 활용하여 Claude Code에게 작업을 위임하고 전체 진행 상태를 관리하도록 합니다.

## Constraints
- Gemini는 직접 프로젝트 코드를 수정하지 않습니다.

## Workflow

### 1. Research & Analysis
Gemini는 다음 도구들을 사용하여 구현에 필요한 컨텍스트를 수집해야 합니다.
- **파일 참조**: `codebase_investigator`를 사용하여 수정 또는 참조가 필요한 핵심 파일들을 식별합니다.
- **코드 심볼 분석**: 수정 또는 참조가 필요한 구체적인 **클래스, 인터페이스, 함수(메서드)**를 파악하여 기록합니다.
- **아키텍처**: 프로젝트 내의 디자인 패턴과 스타일 가이드를 파악합니다.

### 2. Write Planning Report
Research & Analysis 단계에서 수집한 컨텍스트를 harness/task_state.json 파일에 기록합니다.
- phase: 반드시 "PLANNING"으로 설정합니다."
- goal: Compact 하고 명확한 작업목표를 설정합니다.
- files: 참조할 파일을 명시합니다. 반드시 프로젝트에 존재하는 파일만 작성하며 상대경로로 작성합니다. 만약 참조할 파일이 없다면 빈 리스트로 남깁니다.
- symbols: 참조할 매서드를 명시합니다. 반드시 프로젝트에 존재하는 매서드만 명시하며 참조할 매서드가 없다면 빈 리스트로 남깁니다.
- claude_instruction: 클로드에게 명령할 작업을 입력합니다. 명령형으로 입력하며 세부 작업 내용보단 전체적인 목표와 사용할 아키텍처정도만 제시합니다.

```json
{
  "phase": "PLANNING",
  "goal": "Refactor error handling in CourseSearch screen",
  "files": [
    "./domain/usecase/GetCourseSearchResultsUseCase.kt",
    "./presentation/ui/coursesearch/CourseSearchViewModel.kt"
  ],
  "symbols": [
    "GetCourseSearchResultsUseCase.invoke()",
    "CourseSearchViewModel.getSearchResults()",
    "ApiException.ApiRequestFailureException"
  ],
  "claude_instruction": "Refactor GetCourseSearchResultsUseCase to handle API errors. Use TemplateMethod Patterns"
}
```


### 3. Monitoring & Review
- `harness.sh`가 실행되는 동안 상태를 모니터링합니다.
- Claude의 작업이 완료되면(`REVIEW_REQUIRED` 상태), 코드 변경 사항을 검토하고 `review-and-build` 스킬을 사용하여 검증합니다.

## 프롬프트 예시 (Compact English)                                                                                                                                     │
- "Define User DTO: id, name, email. Add Gson annotations."                                                                                                            │
- "Add biometric auth to LoginScreen.kt using Credentials API."                                                                                                        │
- "Fix memory leak in LocationService.kt. Unregister listener in onDestroy."                                                                                           │
