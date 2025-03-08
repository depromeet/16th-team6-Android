package com.depromeet.team6.presentation.ui.home

import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class HomeContract {
    data class HomeUiState(
        val loadState: LoadState = LoadState.Idle,
        val isAlarmRegistered: Boolean = false,
        val isBusDeparted: Boolean = false,
        val showSpeechBubble: Boolean = true
    ) : UiState

    sealed interface HomeSideEffect : UiSideEffect {
        data object DummySideEffect : HomeSideEffect
    }

    sealed class HomeEvent : UiEvent {
        data class DummyEvent(val loadState: LoadState) : HomeEvent()
        data class UpdateAlarmRegistered(val isRegistered: Boolean) : HomeEvent()
        data class UpdateBusDeparted(val isBusDeparted: Boolean) : HomeEvent()
        data class UpdateSpeechBubbleVisibility(val show: Boolean) : HomeEvent()
        data object OnCharacterClick : HomeEvent()
    }
}
