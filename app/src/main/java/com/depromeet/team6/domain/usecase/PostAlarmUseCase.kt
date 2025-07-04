package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.repositoryimpl.AlarmRepositoryImpl
import com.depromeet.team6.domain.usecase.base.ApiRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
import javax.inject.Inject

class PostAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepositoryImpl
) : ApiRequestUseCase<PostAlarmUseCase.Params, Unit>() {

    data class Params(val lastRouteId: String)

    suspend operator fun invoke(lastRouteId: String): Result<Unit> =
        invoke(Params(lastRouteId))

    override suspend fun apiCall(params: Params): Result<Unit> {
        return alarmRepository.postAlarm(
            lastRouteId = params.lastRouteId
        )
    }

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        return when (errorCode) {
            "TOK_001" -> ErrorControlFailureException.NavigateAndShowToastException("로그인 정보가 만료되었습니다. 다시 로그인 해주세요.", Route.Login)
            "TOK_002" -> ErrorControlFailureException.NavigateAndShowToastException("로그인 정보가 만료되었습니다. 다시 로그인 해주세요.", Route.Login)
            "USR_002" -> ErrorControlFailureException.NavigateAndShowToastException("로그인 정보가 만료되었습니다. 다시 로그인 해주세요.", Route.Login)
            "TRS_013" -> ErrorControlFailureException.NavigateAndShowToastException("경로를 찾을 수 없습니다. 다시 한번 등록해주세요.", Route.Home)
            "INTERNAL_SERVER_ERROR" -> ErrorControlFailureException.ShowToastException("알 수 없는 서버 에러입니다.")
            else -> ErrorControlFailureException.ShowToastException("알 수 없는 서버 에러입니다.")
        }
    }
}
