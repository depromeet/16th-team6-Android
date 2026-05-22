package com.depromeet.team6.presentation.ui.itinerary

import android.util.SparseArray
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.RealTimeBusArrival
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class ItineraryContract {
    data class ItineraryUiState(
        val courseDataLoadState: LoadState = LoadState.Idle,
        val itineraryInfo: CourseInfo? = null,
        val busArrivalStatus: SparseArray<List<RealTimeBusArrival>> = SparseArray(),
        val departurePoint: Address? = null,
        val destinationPoint: Address? = null,
        val isAlarmRegistered: Boolean = true,
        val userDeparture: Boolean = false,
        val showOverlayPermissionDialog: Boolean = false,
        val showPermissionSnackbar: Boolean = false
    ) : UiState

    sealed interface ItinerarySideEffect : UiSideEffect {
        data object NavigateHomeWithToast : ItinerarySideEffect
        data object ShowNotificationToastSetAlarmFailed : ItinerarySideEffect
    }

    sealed class ItineraryEvent : UiEvent {
        data class LoadLegsResult(val result: CourseInfo) : ItineraryEvent()
        data object RefreshButtonClicked : ItineraryEvent()
        data class RegisterAlarm(val routeId: String) : ItineraryEvent()
        data object ShowOverlayPermissionDialog : ItineraryEvent()
        data object DismissOverlayPermissionDialog : ItineraryEvent()
        data object ShowPermissionSnackbar : ItineraryEvent()
        data object DismissPermissionSnackbar : ItineraryEvent()
    }
}
