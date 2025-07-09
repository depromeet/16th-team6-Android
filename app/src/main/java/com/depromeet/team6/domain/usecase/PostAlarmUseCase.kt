package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.repositoryimpl.AlarmRepositoryImpl
import com.depromeet.team6.domain.Auth.TOK_001
import com.depromeet.team6.domain.Auth.TOK_002
import com.depromeet.team6.domain.Auth.USR_002
import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.RouteMap.TRS_013
import com.depromeet.team6.domain.ToastMessage.API_ERROR_LOGIN_TOKEN_EXPIRED
import com.depromeet.team6.domain.ToastMessage.API_ERROR_UNKNOWN
import com.depromeet.team6.domain.ToastMessage.API_ERROR__OUT_OF_ROUTE
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
            TOK_001 -> ErrorControlFailureException.NavigateAndShowToastException(API_ERROR_LOGIN_TOKEN_EXPIRED, Route.Login)
            TOK_002 -> ErrorControlFailureException.NavigateAndShowToastException(API_ERROR_LOGIN_TOKEN_EXPIRED, Route.Login)
            USR_002 -> ErrorControlFailureException.NavigateAndShowToastException(API_ERROR_LOGIN_TOKEN_EXPIRED, Route.Login)
            TRS_013 -> ErrorControlFailureException.NavigateAndShowToastException(API_ERROR__OUT_OF_ROUTE, Route.Home)
            INTERNAL_SERVER_ERROR -> ErrorControlFailureException.ShowToastException(API_ERROR_UNKNOWN)
            else -> ErrorControlFailureException.ShowToastException(API_ERROR_UNKNOWN)
        }
    }
}
