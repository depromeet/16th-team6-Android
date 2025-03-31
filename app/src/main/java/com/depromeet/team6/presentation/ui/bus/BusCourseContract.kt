package com.depromeet.team6.presentation.ui.bus

import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class BusCourseContract {
    data class BusCourseUiState(
        val loadState: LoadState = LoadState.Idle,
        val subtypeIdx: Int = 1
    ) : UiState

    sealed interface BusCourseSideEffect : UiSideEffect {
        data object NavigateToBackStack : BusCourseSideEffect
    }

    sealed class BusCourseEvent : UiEvent {
        data class SetScreenLoadState(val loadState: LoadState) : BusCourseEvent()
    }
}
