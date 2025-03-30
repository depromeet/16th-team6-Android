package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.repository.LocationsRepository
import retrofit2.Response
import javax.inject.Inject

class DeleteAllSearchHistoryUseCase @Inject constructor(
    private val locationsRepository: LocationsRepository
) {
    suspend operator fun invoke(): Response<Unit> =
        locationsRepository.deleteAllSearchHistory()
}