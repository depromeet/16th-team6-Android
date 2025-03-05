package com.depromeet.team6.presentation.ui.login

import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class LoginContract {
    data class LoginUiState(
        val loadState: LoadState = LoadState.Idle,
        val authTokenLoadState: LoadState = LoadState.Idle,
        val isUserRegisteredState: LoadState = LoadState.Idle
    ) : UiState

    sealed interface LoginSideEffect : UiSideEffect {
        data object NavigateToOnboarding : LoginSideEffect
        data object NavigateToHome : LoginSideEffect
    }

    sealed class LoginEvent : UiEvent {
        data class SetAuthToken(val authTokenLoadState: LoadState, val loadState: LoadState) : LoginEvent()
        data class GetLogin(val loadState: LoadState) : LoginEvent()
        data class GetCheckUserRegistered(val isUserRegisteredState: LoadState) : LoginEvent()
    }
}
