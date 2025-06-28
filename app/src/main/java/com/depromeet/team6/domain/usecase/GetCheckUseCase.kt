package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.Auth.ATH_001
import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.RequestFormat.REQ_001
import com.depromeet.team6.domain.RequestFormat.REQ_002
import com.depromeet.team6.domain.ToastMessage.LOGIN_DATA_EXPIRED
import com.depromeet.team6.domain.ToastMessage.NETWORK
import com.depromeet.team6.domain.ToastMessage.UNKNOWN
import com.depromeet.team6.domain.repository.AuthRepository
import com.depromeet.team6.domain.usecase.base.ApiRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCheckUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : ApiRequestUseCase<GetCheckUseCase.Params, Boolean>() {
    data class Params(val authorization: String, val provider: Int)

    suspend operator fun invoke(
        authorization: String, provider: Int
    ): Result<Boolean> = invoke(Params(authorization = authorization, provider = provider))

    override suspend fun apiCall(params: Params): Result<Boolean> =
        authRepository.getCheck(authorization = params.authorization, provider = params.provider)


    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException = when (errorCode) {
        REQ_001, REQ_002 -> ErrorControlFailureException.ReportDiscordWithToast(UNKNOWN)
        ATH_001 -> ErrorControlFailureException.ShowToastException(LOGIN_DATA_EXPIRED)
        INTERNAL_SERVER_ERROR -> ErrorControlFailureException.ShowToastException(NETWORK)
        else -> ErrorControlFailureException.ShowToastException(UNKNOWN)
    }

}

