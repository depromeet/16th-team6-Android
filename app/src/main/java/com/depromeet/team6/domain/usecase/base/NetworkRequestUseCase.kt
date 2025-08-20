package com.depromeet.team6.domain.usecase.base

import com.depromeet.team6.data.dataremote.model.response.base.ApiException
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import kotlinx.coroutines.CancellationException

abstract class NetworkRequestUseCase<P, R> {

    // 템플릿 메서드 패턴
    suspend operator fun invoke(params: P): Result<R> {
        return try {
            val apiResult = apiCall(params)

            apiResult.fold(
                onSuccess = {
                    Result.success(it)
                },
                onFailure = { apiException ->
                    return when (apiException) {
                        is ApiException.NetworkFailureException -> {
                            Result.failure(ErrorControlFailureException.ShowToastException("알 수 없는 서버에러입니다."))
                        }

                        is ApiException.ApiRequestFailureException -> {
                            val errorControlFailureException = apiExceptionMapper(apiException.errorCode)
                            Result.failure(errorControlFailureException)
                        }

                        else -> {
                            Result.failure(ErrorControlFailureException.ShowToastException("알 수 없는 서버에러입니다."))
                        }
                    }
                }
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: ApiException.NetworkFailureException) {
            Result.failure(e)
        }
    }

    /** API 호출로직 */
    protected abstract suspend fun apiCall(params: P): Result<R>

    /** 응답오류 -> 뷰에서 처리할 예외로 매핑 */
    protected abstract fun apiExceptionMapper(errorCode: String): ErrorControlFailureException
}
