package com.depromeet.team6.presentation.ui.itinerary

import com.depromeet.team6.presentation.model.course.LastTransportInfo
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class ItineraryContract {
    data class ItineraryUiState(
        val courseDataLoadState: LoadState = LoadState.Idle,
        val itineraryInfo : LastTransportInfo? = null
    ) : UiState

    sealed interface ItinerarySideEffect : UiSideEffect

    sealed class ItineraryEvent : UiEvent {
        data object RegisterAlarm : ItineraryEvent()
        data class LoadLegsResult(val result: LastTransportInfo) : ItineraryEvent()
    }
}
