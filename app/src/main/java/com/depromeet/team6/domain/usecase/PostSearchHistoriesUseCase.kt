package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.model.SearchHistory
import com.depromeet.team6.domain.repository.LocationsRepository
import retrofit2.Response
import javax.inject.Inject

class PostSearchHistoriesUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) {
    suspend operator fun invoke(searchHistory: SearchHistory): Response<Unit> =
        locationsRepository.postSearchHistories(requestSearchHistoryDto = searchHistory)
}
