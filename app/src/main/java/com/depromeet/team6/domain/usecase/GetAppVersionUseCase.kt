package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.Auth.APP_001
import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.ToastMessage.API_ERROR_INVALID_PLATFORM
import com.depromeet.team6.domain.ToastMessage.API_ERROR_UNKNOWN
import com.depromeet.team6.domain.repository.VersionRepository
import com.depromeet.team6.domain.usecase.base.NetworkRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAppVersionUseCase @Inject constructor(
    private val versionRepository: VersionRepository
) : NetworkRequestUseCase<Unit, String>() {

    suspend operator fun invoke(): Result<String> =
        invoke(Unit)

    override suspend fun apiCall(params: Unit): Result<String> {
        return versionRepository.getVersion()
    }

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        return when (errorCode) {
            APP_001 -> ErrorControlFailureException.ShowToastException(
                API_ERROR_INVALID_PLATFORM
            )

            INTERNAL_SERVER_ERROR -> ErrorControlFailureException.ShowToastException(API_ERROR_UNKNOWN)
            else -> ErrorControlFailureException.ShowToastException(API_ERROR_UNKNOWN)
        }
    }
}
