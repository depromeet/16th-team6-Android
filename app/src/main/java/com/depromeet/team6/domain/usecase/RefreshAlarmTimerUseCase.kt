package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.Auth.USR_002
import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.RequestFormat.REQ_001
import com.depromeet.team6.domain.RequestFormat.REQ_002
import com.depromeet.team6.domain.ToastMessage.API_ERROR_ALARM_REFRESH_FAILED
import com.depromeet.team6.domain.ToastMessage.API_ERROR_LOGIN_TOKEN_EXPIRED
import com.depromeet.team6.domain.ToastMessage.API_ERROR_UNKNOWN
import com.depromeet.team6.domain.repository.AlarmRepository
import com.depromeet.team6.domain.usecase.base.NetworkRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
import javax.inject.Inject

class RefreshAlarmTimerUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository
) : NetworkRequestUseCase<Unit, String>() {

    suspend operator fun invoke(): Result<String> = invoke(Unit)
    override suspend fun apiCall(params: Unit): Result<String> {
        return alarmRepository.refreshAlarm()
    }

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        return when (errorCode) {
            REQ_001 -> ErrorControlFailureException.ReportDiscordWithToast(toastMessage = API_ERROR_UNKNOWN)
            REQ_002 -> ErrorControlFailureException.ReportDiscordWithToast(toastMessage = API_ERROR_UNKNOWN)
            USR_002 -> ErrorControlFailureException.NavigateAndShowToastException(route = Route.Login, toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED)
            INTERNAL_SERVER_ERROR -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_UNKNOWN)
            "URT_001" -> TODO()
            "URT_002" -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_ALARM_REFRESH_FAILED)
            else -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_UNKNOWN)
        }
    }
}
