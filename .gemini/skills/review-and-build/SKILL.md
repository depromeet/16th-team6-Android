---
name: review-and-build
description: Autonomous workflow for code review, direct fixing, building, and testing in Android projects using Gradle. Use when you need to ensure code quality and stability after modifications or before delivery.
---

## 절차

이 스킬은 코드 변경 사항이 발생한 후 또는 정기적으로 프로젝트의 품질과 안정성을 확보하기 위해 사용됩니다. 모든 과정은 자율적으로 진행되며, 실패 시 스스로 원인을 파악하여 수정을 시도합니다.

1. **코드 리뷰 및 자동 수정**
   - 대상 파일 및 관련 코드의 보안 취약점, 아키텍처(MVI, Clean Architecture) 준수 여부, 코드 품질을 검토합니다.
   - 발견된 문제는 사용자에게 묻지 않고 직접 수정합니다. (예: `ktlint` 스타일 위반, 메모리 누수 위험, 잘못된 Flow 처리 등)

2. **빌드 검증 (Build Verification)**
   - `./gradlew assembleDebug` 명령을 실행하여 프로젝트가 정상적으로 컴파일되는지 확인합니다.
   - 빌드 실패 시 에러 로그를 분석하여 원인(종속성 문제, 컴파일 에러 등)을 파악하고 코드를 수정한 뒤 재빌드합니다. (최대 3회 재시도)

3. **테스트 및 정적 분석 (Test & Static Analysis)**
   - 유닛 테스트(`./gradlew testDebugUnitTest`)를 실행합니다.
   - 코드 스타일 및 린트 검사(`./gradlew ktlintCheck`)를 실행합니다.
   - 테스트나 린트 실패 시 원인을 분석하여 수정 후 재실행합니다. (최대 3회 재시도)

4. **최종 보고 (Completion Report)**
   - 수정된 내용에 대한 요약(보안, 품질, 아키텍처 측면)을 제공합니다.
   - 빌드 및 테스트 결과(성공 여부, 통과한 테스트 수 등)를 리포트합니다.

## 원칙
- **자율성**: 각 단계에서 발생하는 문제는 스스로 해결책을 찾아 적용합니다. 사용자에게 허락을 구하지 않고 수정을 진행합니다.
- **최대 재시도**: 한 단계에서 3회 이상 해결에 실패할 경우에만 사용자에게 보고하고 가이드를 요청합니다.
- **안정성 우선**: 빌드가 깨진 상태로 작업을 종료하지 않습니다.
