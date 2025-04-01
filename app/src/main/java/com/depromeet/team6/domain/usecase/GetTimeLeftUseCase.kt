package com.depromeet.team6.domain.usecase

import com.depromeet.team6.domain.repository.TimeLeftRepository
import javax.inject.Inject

class GetTimeLeftUseCase @Inject constructor(
    private val timeLeftRepository: TimeLeftRepository
) {
    suspend operator fun invoke(routeId: String): Result<Int> =
        timeLeftRepository.getDepartureRemainingTime(routeId = routeId)
}
