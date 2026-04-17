package com.depromeet.team6.domain.usecase.base

import com.depromeet.team6.data.dataremote.model.response.base.ApiException
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

/**
 * Flow 기반 네트워크 요청 UseCase 추상 기반 클래스.
 *
 * [mapNetworkErrors] 확장 함수를 통해 [ApiException]을 [ErrorControlFailureException]으로
 * 일관되게 변환한다. 하위 클래스는 [apiExceptionMapper]를 구현하여 비즈니스 에러코드별
 * 예외 처리 전략을 정의한다.
 */
abstract class FlowNetworkRequestUseCase<R> {

    /**
     * 비즈니스 에러코드 → [ErrorControlFailureException] 매핑.
     * [ApiException.ApiRequestFailureException] 수신 시 호출된다.
     */
    protected abstract fun apiExceptionMapper(errorCode: String): ErrorControlFailureException

    /**
     * Flow 스트림에서 발생한 [ApiException]을 [ErrorControlFailureException]으로 변환하는
     * 확장 함수. 이미 [ErrorControlFailureException]으로 변환된 예외는 그대로 재전파한다.
     */
    protected fun Flow<R>.mapNetworkErrors(): Flow<R> = catch { exception ->
        throw when (exception) {
            is ErrorControlFailureException -> exception
            is ApiException.ApiRequestFailureException ->
                apiExceptionMapper(exception.errorCode)
            is ApiException.NetworkFailureException ->
                ErrorControlFailureException.ShowToastException(exception.errorMessage)
            else ->
                ErrorControlFailureException.ShowToastException("알 수 없는 오류가 발생했습니다.")
        }
    }
}
