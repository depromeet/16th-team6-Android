package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.ToastMessage.API_ERROR_INVALID_LOCATION
import com.depromeet.team6.domain.ToastMessage.API_ERROR_NETWORK_FAILURE
import com.depromeet.team6.domain.ToastMessage.API_ERROR_UNKNOWN
import com.depromeet.team6.domain.model.RouteLocation
import com.depromeet.team6.domain.repository.TaxiCostRepository
import com.depromeet.team6.domain.usecase.base.NetworkRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTaxiCostUseCase @Inject constructor(
    private val taxiCostRepository: TaxiCostRepository
) : NetworkRequestUseCase<GetTaxiCostUseCase.Params, Int>() {

    data class Params(val routeLocation: RouteLocation)
    suspend operator fun invoke(routeLocation: RouteLocation): Result<Int> =
        taxiCostRepository.getTaxiCost(routeLocation = routeLocation)

    // 택시 비용 저장
    suspend fun saveTaxiCost(cost: Int) =
        taxiCostRepository.saveTaxiCost(cost)

    // 저장된 택시 비용 관찰
    fun observeTaxiCost(): Flow<Int> =
        taxiCostRepository.observeTaxiCost()

    // 마지막으로 저장된 택시 비용 조회
    suspend fun getLastSavedTaxiCost(): Int =
        taxiCostRepository.getLastSavedTaxiCost()

    // 잠금화면 용 택시 비용 조회 (SharedPreferences 사용)
    suspend fun getPersistedTaxiCostForLockScreen(): Int =
        taxiCostRepository.getPersistedTaxiCostForLockScreen()

    override suspend fun apiCall(params: Params): Result<Int> {
        return taxiCostRepository.getTaxiCost(routeLocation = params.routeLocation)
    }

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        return when (errorCode) {
            "LOC_003" -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_INVALID_LOCATION)
            "LOC_004" -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_INVALID_LOCATION)
            "TRS_002" -> ErrorControlFailureException.ReportDiscordWithToast(toastMessage = API_ERROR_NETWORK_FAILURE)
            "INTERNAL_SERVER_ERROR" -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_UNKNOWN)
            else -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_UNKNOWN)
        }
    }
}
