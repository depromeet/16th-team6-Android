package com.depromeet.team6.presentation.ui.lock

import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState

class LockContract {
    data class LockUiState(
        val timeLeft: Int = INITIAL_TIME,
        val isTimerFinished: Boolean = false,
        val taxiCost: Int = 0
    ) : UiState {
        companion object {
            const val INITIAL_TIME = 5
        }
    }

    sealed class LockSideEffect : UiSideEffect {
        data object CloseScreen : LockSideEffect()
        data class NavigateToHome(val userDeparted: Boolean) : LockSideEffect()
    }

    sealed class LockEvent : UiEvent {
        data object OnTimerFinish : LockEvent()
        data object OnCloseClick : LockEvent()
        data object OnDepartureClick : LockEvent()
        data object OnLateClick : LockEvent()
    }
}
