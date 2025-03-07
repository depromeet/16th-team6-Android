package com.depromeet.team6.presentation.ui.home

import com.depromeet.team6.domain.model.DummyData
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class HomeContract {
    data class HomeUiState(
        val loadState: LoadState = LoadState.Idle,
        val dummyData: List<DummyData> = emptyList()
    ) : UiState

    sealed interface HomeSideEffect : UiSideEffect {
        data object NavigateToLogin : HomeSideEffect
    }

    sealed class HomeEvent : UiEvent {
        data class DummyEvent(val loadState: LoadState) : HomeEvent()
        data class LogoutClicked(val loadState: LoadState) : HomeEvent()
        data class WithDrawClicked(val loadState: LoadState) : HomeEvent()
    }
}
