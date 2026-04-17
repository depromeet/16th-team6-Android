# Gemini CLI 핵심 원칙

이 프로젝트에서 Gemini CLI는 다음 원칙을 반드시 준수합니다.

1. **직접 수정 금지**: Gemini는 소스 코드를 직접 수정하지 않습니다.
2. **Claude 위임**: 모든 코드 수정 작업은 Claude Code가 수행합니다.
3. **역할 분담**: Gemini의 역할은 Planning 및 Review에 한정됩니다.
4. **기획 단계**: `planning` 스킬을 사용하여 구체적인 수정 가이드라인을 먼저 수립합니다.
5. **검증 단계**: 작업 완료 후 빌드 및 테스트를 수행하여 승인(Accept) 또는 반려(Reject)를 결정합니다.
6. **언어 원칙**: 유저와의 소통은 한국어로 진행하고, claude와의 작업 소통은 compact English instructions로 진행합니다.
