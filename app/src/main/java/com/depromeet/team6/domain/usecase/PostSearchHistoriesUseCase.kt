package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.Auth.TOK_001
import com.depromeet.team6.domain.Auth.TOK_002
import com.depromeet.team6.domain.Auth.USR_002
import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.ToastMessage
import com.depromeet.team6.domain.ToastMessage.API_ERROR_LOGIN_TOKEN_EXPIRED
import com.depromeet.team6.domain.model.SearchHistory
import com.depromeet.team6.domain.repository.LocationsRepository
import com.depromeet.team6.domain.usecase.base.ApiRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
import javax.inject.Inject

class PostSearchHistoriesUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) : ApiRequestUseCase<PostSearchHistoriesUseCase.Params, Unit>() {

    data class Params(val searchHistory: SearchHistory)

    suspend operator fun invoke(searchHistory: SearchHistory): Result<Unit> = invoke(Params(searchHistory))

    override suspend fun apiCall(params: Params): Result<Unit> {
        return locationsRepository.postSearchHistories(requestSearchHistoryDto = params.searchHistory)
    }

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException =
        when (errorCode) {
            TOK_001 -> ErrorControlFailureException.ShowToastException(API_ERROR_LOGIN_TOKEN_EXPIRED)
            TOK_002 -> ErrorControlFailureException.ShowToastException(API_ERROR_LOGIN_TOKEN_EXPIRED)
            USR_002 -> ErrorControlFailureException.NavigateAndShowToastException(
                toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED,
                route = Route.Login
            )
            INTERNAL_SERVER_ERROR -> ErrorControlFailureException.ShowToastException(ToastMessage.API_ERROR_NETWORK_FAILURE)
            else -> ErrorControlFailureException.ShowToastException(ToastMessage.API_ERROR_UNKNOWN)
        }
}
