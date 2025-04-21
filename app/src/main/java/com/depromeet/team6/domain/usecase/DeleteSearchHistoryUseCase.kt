package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.model.SearchHistory
import com.depromeet.team6.domain.repository.LocationsRepository
import retrofit2.Response
import javax.inject.Inject

class DeleteSearchHistoryUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) {
    suspend operator fun invoke(searchHistory: SearchHistory): Response<Unit> =
        locationsRepository.deleteSearchHistory(name = searchHistory.name, lat = searchHistory.lat, lon = searchHistory.lon, businessCategory = searchHistory.businessCategory, address = searchHistory.address)
}
