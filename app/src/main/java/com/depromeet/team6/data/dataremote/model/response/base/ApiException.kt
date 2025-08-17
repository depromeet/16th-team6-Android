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
        const val NETWORK_NOT_AVAILABLE = "NET_000"
        const val CANNOT_FIND_SERVER_HOST = "NET_001"
        const val REQUEST_TIMEOUT = "NET_002"
        const val UNKNOWN_FAILURE = "NET_003"
    }

    /** Network Failure
     * - 기기가 오프라인, 타임아웃, DNS 실패, SSL 오류,
     *   혹은 기타 이유로 정상적인 HTTP 요청을 하지 못한 상황
     */
    sealed class NetworkFailureException(
        errorCode: String,
        errorMessage: String = "네트워크 연결을 확인해 주세요."
    ) : ApiException(NETWORK_NOT_AVAILABLE, errorMessage) {
        // Timeout, DNS 실패, 연결 불가 등 구체적인 네트워크 오류를 객체(object)나 클래스로 정의
        data object Timeout : NetworkFailureException(REQUEST_TIMEOUT, "요청 시간이 초과되었습니다.")

        data object NoConnection : NetworkFailureException(NETWORK_NOT_AVAILABLE, "네트워크 연결을 확인해주세요.")

        data object CannotFindHost : NetworkFailureException(CANNOT_FIND_SERVER_HOST, "서버 주소를 찾을 수 없습니다.")

        data class UnknownFailure(val detailMessage: String) : NetworkFailureException(UNKNOWN_FAILURE, detailMessage)
    }

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
