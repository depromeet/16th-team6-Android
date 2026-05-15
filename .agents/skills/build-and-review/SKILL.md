---
name: build-and-review
description: Validate executor-written code in the harness.sh workflow by running build checks and performing a strict critical-only review gate. Use when phase is REVIEW_REQUIRED and you must decide between COMPLETED and REPLANNING. Report only critical defects (build failure, crash-risk, data-loss, security/privacy, broken requirements) and skip minor style or convention comments.
---

# Harness Critical Review

Execute this skill as the reviewer gate for `harness/task_state.json` after executor work is finished.

## Review Workflow

1. Load `harness/task_state.json` and confirm the current phase is `REVIEW_REQUIRED`.
2. Identify executor changes from git diff and prioritize changed files for review.
3. Run build verification first.
4. Perform a risk-focused review limited to critical issues.
5. Decide the next phase:
6. Set `COMPLETED` when no critical issue exists.
7. Set `REPLANNING` only when at least one critical issue exists.
8. Write concise feedback that is directly actionable by planner/executor.

## Build Verification Rules

- Prefer project-standard Android build checks (for example `./gradlew :app:compileDebugKotlin` and any task used by harness CI).
- Treat build failure as critical.
- Include the failing command and the top error signal in feedback.

## Critical-Issue Criteria

Classify as critical only when the issue has high user or system impact, such as:

1. Build/test gate broken so delivery cannot proceed.
2. Runtime crash path or severe malfunction in core flow.
3. Data corruption, irreversible data loss, or privacy/security exposure.
4. Explicit requirement not implemented or broken in a release-blocking way.
5. Infinite loop, deadlock, ANR-risk, or resource leak likely to break production.

Skip reporting for minor naming/style/convention nits, non-blocking refactors, or subjective preferences.

## Decision Policy

- If critical issues count is `0`: keep feedback brief and approve by setting `phase=COMPLETED`.
- If critical issues count is `>=1`: reject by setting `phase=REPLANNING`.
- Never trigger `REPLANNING` for minor issues.

## Output Contract

Return feedback in a compact, machine-friendly structure:

1. `verdict`: `APPROVED` or `REJECTED`
2. `critical_issues`: array
3. `build_check`: command(s) executed + pass/fail
4. `phase_update`: `COMPLETED` or `REPLANNING`
5. `feedback`: concise remediation steps only for critical items

When reporting issues, include:

1. File path
2. Failure mode
3. Why it is critical
4. Minimal fix direction

## Example Trigger Prompts

Use this skill for prompts like:

1. "harness REVIEW_REQUIRED 단계에서 executor 결과를 빌드 검증하고 크리티컬만 리뷰해."
2. "치명적 이슈가 있을 때만 REPLANNING으로 보내고 아니면 완료 처리해."
