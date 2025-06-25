package com.depromeet.team6.domain.usecase.base

import com.depromeet.team6.data.dataremote.model.response.base.ApiException
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException

abstract class ApiRequestUseCase<P, R> {

    // 템플릿 메서드 ― 흐름 고정 (final 처럼 사용)
    suspend operator fun invoke(params: P): R {
        val apiResult = apiCall(params) // 1단계 (추상)
        return apiResult.getOrElse { apiException ->
            throw when (apiException) {
                is ApiException.NetworkFailureException -> {
                    // 여기서 errorCode, errorMessage 접근 가능
                    ErrorControlFailureException.ShowToastException("알 수 없는 서버에러입니다.")
                }

                is ApiException.ApiRequestFailureException -> {
                    apiExceptionMapper(apiException.errorCode)
                }

                else -> {
                    ErrorControlFailureException.ShowToastException("알 수 없는 서버에러입니다.")
                }
            }
        }
    }

    /** API 호출로직 */
    protected abstract suspend fun apiCall(params: P): Result<R>

    /** 응답오류 -> 뷰에서 처리할 예외로 매핑 */
    protected abstract fun apiExceptionMapper(errorCode: String): ErrorControlFailureException
}
