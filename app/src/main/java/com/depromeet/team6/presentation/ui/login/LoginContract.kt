package com.depromeet.team6.presentation.ui.login

import com.depromeet.team6.domain.model.DummyData
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class LoginContract {
    data class LoginUiState(
        val loadState: LoadState = LoadState.Idle,
        val authTokenLoadState: LoadState = LoadState.Idle,
        ) : UiState

    sealed interface LoginSideEffect : UiSideEffect {
        data object NavigateToOnboarding : LoginSideEffect
        data object NavigateToHome : LoginSideEffect
    }

    sealed class LoginEvent : UiEvent {
        data class PostLogin(val loadState: LoadState) : LoginEvent()
        data class SetAuthToken(val authTokenLoadState: LoadState) : LoginEvent()
    }
}
