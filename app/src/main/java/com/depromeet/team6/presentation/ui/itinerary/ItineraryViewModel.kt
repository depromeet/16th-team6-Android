package com.depromeet.team6.presentation.ui.itinerary

import com.depromeet.team6.domain.usecase.LoadMockSearchDataUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ItineraryViewModel @Inject constructor (
    val loadMockData : LoadMockSearchDataUseCase
) : BaseViewModel<ItineraryContract.ItineraryUiState, ItineraryContract.ItinerarySideEffect, ItineraryContract.ItineraryEvent>() {
    override fun createInitialState(): ItineraryContract.ItineraryUiState = ItineraryContract.ItineraryUiState()

    override suspend fun handleEvent(event: ItineraryContract.ItineraryEvent) {
        when (event) {
            is ItineraryContract.ItineraryEvent.LoadLegsResult -> {
                setState { copy(courseData = event.legsResult) }
            }
            ItineraryContract.ItineraryEvent.RegisterAlarm -> TODO()
        }
    }

    fun getLegs() {
        val mockData = loadMockData()
        setEvent(ItineraryContract.ItineraryEvent.LoadLegsResult(mockData[0].legs))
    }
}