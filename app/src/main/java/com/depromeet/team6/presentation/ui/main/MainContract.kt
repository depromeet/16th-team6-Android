package com.depromeet.team6.presentation.ui.main

import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class MainContract {
    data class MainState(
        val splashState: LoadState = LoadState.Loading,
        val autoLogin: Boolean = false
    ) : UiState

    sealed interface MainSideEffect : UiSideEffect {
        data object ShowUpdateRequiredDialog : MainSideEffect
        data object ShowUpdateOptionalDialog : MainSideEffect
    }

    sealed class MainEvent : UiEvent
}
