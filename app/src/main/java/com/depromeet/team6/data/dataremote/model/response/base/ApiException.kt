package com.depromeet.team6.data.dataremote.model.response.base

import java.io.IOException

/**
 * API 요청시 발생하는 예외
 * 반드시 View 계층에서 처리해야 하는 예외이므로 CheckedException을 상속하도록 구현해주세요.
 *
 * @property errorCode    서버·클라이언트 식별용 코드(예: "TRS_019", "USR_001")
 * @property errorMessage 로그용 메시지
 *
 */
sealed class ApiException(
    val errorCode: String,
    val errorMessage: String
) : IOException() {

    companion object {
        const val NETWORK_ERROR_CODE = "NET_001"
    }

    /** Network Failure
     * - 기기가 오프라인, 타임아웃, DNS 실패, SSL 오류,
     *   혹은 기타 이유로 정상적인 HTTP 요청을 하지 못한 상황
     */
    class NetworkFailureException(
        errorMessage: String = "네트워크 연결을 확인해 주세요."
    ) : ApiException(NETWORK_ERROR_CODE, errorMessage)

    /** Business Logic Failure
     * - HTTP 통신은 성공했지만, 서버가
     *   비즈니스 규약상의 오류 코드를 응답한 경우
     *   (예: "TRS_011": 거리가 너무 가까움,  "USR_002": 사용자 미존재)
     * ------------------------------------------------------------ */
    class ApiRequestFailureException(
        errorCode: String,
        errorMessage: String
    ) : ApiException(errorCode, errorMessage)
}
