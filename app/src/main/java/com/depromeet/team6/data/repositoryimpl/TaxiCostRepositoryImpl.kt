package com.depromeet.team6.data.repositoryimpl

import android.content.Context
import com.depromeet.team6.data.dataremote.datasource.TaxiCostRemoteDataSource
import com.depromeet.team6.data.dataremote.model.request.taxi.RequestTaxiCostDto
import com.depromeet.team6.domain.model.RouteLocation
import com.depromeet.team6.domain.repository.TaxiCostRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaxiCostRepositoryImpl @Inject constructor(
    private val taxiCostRemoteDataSource: TaxiCostRemoteDataSource,
    @ApplicationContext private val context: Context
) : TaxiCostRepository {

    private val sharedPreferences = context.getSharedPreferences("TaxiCostPreference", Context.MODE_PRIVATE)

    private val _taxiCost = MutableStateFlow(0)

    override suspend fun getTaxiCost(routeLocation: RouteLocation): Result<Int> =
        taxiCostRemoteDataSource.getTaxiCost(
            requestTaxiDto = RequestTaxiCostDto(
                routeLocation.startLat,
                routeLocation.startLon,
                routeLocation.endLat,
                routeLocation.endLon
            )
        ).also { result ->
            result.getOrNull()?.let { cost ->
                _taxiCost.value = cost
                if (cost > 0) {
                    saveToSharedPreferences(cost)
                }
            }
        }

    override fun observeTaxiCost(): Flow<Int> = _taxiCost.asStateFlow()

    override suspend fun saveTaxiCost(taxiCost: Int) {
        _taxiCost.value = taxiCost
        if (taxiCost > 0) {
            saveToSharedPreferences(taxiCost)
        }
    }

    override suspend fun getLastSavedTaxiCost(): Int = _taxiCost.value

    override suspend fun getPersistedTaxiCostForLockScreen(): Int {
        val savedCost = getSharedPrefTaxiCost()
        return if (_taxiCost.value <= 0 && savedCost > 0) {
            savedCost
        } else {
            _taxiCost.value
        }
    }

    private fun saveToSharedPreferences(taxiCost: Int) {
        sharedPreferences.edit().apply {
            putInt(TAXI_COST_KEY, taxiCost)
            apply()
        }
    }

    private fun getSharedPrefTaxiCost(): Int {
        return sharedPreferences.getInt(TAXI_COST_KEY, 0)
    }

    companion object {
        private const val TAXI_COST_KEY = "saved_taxi_cost"
    }
}
