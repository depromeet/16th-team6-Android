package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.model.RouteLocation
import com.depromeet.team6.domain.repository.TaxiCostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTaxiCostUseCase @Inject constructor(
    private val taxiCostRepository: TaxiCostRepository
) {
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
}
