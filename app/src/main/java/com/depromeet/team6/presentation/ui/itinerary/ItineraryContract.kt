package com.depromeet.team6.presentation.ui.itinerary

import android.util.SparseArray
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.RealTimeBusArrival
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.presentation.util.DefaultLatLng.DEFAULT_LAT
import com.depromeet.team6.presentation.util.DefaultLatLng.DEFAULT_LNG
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.android.gms.maps.model.LatLng

class ItineraryContract {
    data class ItineraryUiState(
        val courseDataLoadState: LoadState = LoadState.Idle,
        val itineraryInfo: CourseInfo? = null,
        val busArrivalStatus: SparseArray<RealTimeBusArrival> = SparseArray(),
        val departurePoint: Address? = null,
        val destinationPoint: Address? = null,
        val currentLocation: LatLng = LatLng(
            DEFAULT_LAT,
            DEFAULT_LNG
        )
    ) : UiState

    sealed interface ItinerarySideEffect : UiSideEffect

    sealed class ItineraryEvent : UiEvent {
        data class LoadLegsResult(val result: CourseInfo) : ItineraryEvent()
        data object RefreshButtonClicked : ItineraryEvent()
        data class CurrentLocationClicked(val location: LatLng) : ItineraryEvent()
    }
}
