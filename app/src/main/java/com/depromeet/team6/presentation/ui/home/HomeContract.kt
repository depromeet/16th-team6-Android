package com.depromeet.team6.presentation.ui.home

import com.depromeet.team6.domain.model.course.WayPoint
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class HomeContract {
    data class HomeUiState(
        val loadState: LoadState = LoadState.Idle,
        val isAlarmRegistered: Boolean = false,
        val isBusDeparted: Boolean = false,
        val showSpeechBubble: Boolean = true,
        val departurePoint: WayPoint = WayPoint(
            name = "건국대학교",
            latitude = 37.540957955055,
            longitude = 127.08278172427
        ),
        val destinationPoint : WayPoint = WayPoint(
            name = "우리집",
            latitude = 37.303534788694,
            longitude = 127.01085807594
        ),
        val logoutState: Boolean = false
    ) : UiState

    sealed interface HomeSideEffect : UiSideEffect {
        data object NavigateToMypage : HomeSideEffect
        data object NavigateToItinerary : HomeSideEffect
    }

    sealed class HomeEvent : UiEvent {
        data class DummyEvent(val loadState: LoadState) : HomeEvent()
        data class UpdateAlarmRegistered(val isRegistered: Boolean) : HomeEvent()
        data class UpdateBusDeparted(val isBusDeparted: Boolean) : HomeEvent()
        data class UpdateSpeechBubbleVisibility(val show: Boolean) : HomeEvent()
        data object OnCharacterClick : HomeEvent()
    }
}
