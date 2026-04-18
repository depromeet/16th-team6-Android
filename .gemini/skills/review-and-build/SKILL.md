---
name: review-and-build
description: Android 프로젝트의 빌드 및 테스트를 통해 변경 사항을 검증하고, 오류 발생 시 Claude를 위한 수정 지침을 생성합니다.
---
# Instructions

이 스킬은 `harness.sh`의 `REVIEW_REQUIRED` 단계에서 호출되어, Claude가 수정한 코드가 정상적으로 빌드되고 작동하는지 검증합니다. **절대로 직접 코드를 수정하지 않습니다.**

## Workflow

### 1. Build & Test Execution
- `./gradlew assembleDebug` 와 `./gradlew test`를 실행하여 빌드 및 유닛 테스트를 수행합니다.
- 특정 화면 수정 시 관련 테스트 케이스가 있다면 이를 우선적으로 실행합니다.

### 2. Error Analysis (Failure Case)
- 빌드 또는 테스트 실패 시, 로그를 분석하여 원인이 된 파일, 라인, 오류 메시지를 파악합니다.
- **수정 지시 생성**: 분석된 내용을 바탕으로 Claude가 즉시 수정에 착수할 수 있도록 구체적인 English Instruction을 생성합니다.
- **harness 피드백**: 생성된 지침을 `task_state.json`의 `feedback` 필드에 기록하고, `phase`를 `REPLANNING`으로 변경하도록 제안합니다.

### 3. Approval (Success Case)
- 빌드 및 테스트가 모두 통과하면 변경 사항의 안정성을 선언합니다.
- `task_state.json`의 `phase`를 `COMPLETED`로 변경하도록 제안합니다.

## Constraints
- **수정 금지**: 어떤 경우에도 `replace`나 `write_file` 등을 사용하여 소스 코드를 직접 수정하지 않습니다.
- **Claude 위임**: 모든 수정 작업은 오직 Claude(android-executor)만이 수행해야 하며, Gemini는 지시자 역할을 유지합니다.
- **로그 요약**: 방대한 빌드 로그 전체를 전달하기보다, 핵심 오류 메시지와 컨텍스트 위주로 요약하여 전달합니다.

## 피드백 예시 (To Claude)
- "Build failed in CourseSearchViewModel.kt:125. Unresolved reference 'ErrorType'. Please import com.depromeet.team6.presentation.model.ErrorType."
- "Unit test 'getSearchResults_success' failed. Expected NoResult state but got Loading. Check the flow emission timing in GetCourseSearchResultsUseCase."
