package com.depromeet.team6.presentation.ui.login

import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState

class LoginContract {
    @OptIn(ExperimentalPagerApi::class)
    data class LoginUiState(
        val loadState: LoadState = LoadState.Idle,
        val authTokenLoadState: LoadState = LoadState.Idle,
        val isUserRegisteredState: LoadState = LoadState.Idle,
        val pagerState: PagerState = PagerState()
    ) : UiState

    sealed interface LoginSideEffect : UiSideEffect {
        data object NavigateToOnboarding : LoginSideEffect
        data object NavigateToHome : LoginSideEffect
    }

    @OptIn(ExperimentalPagerApi::class)
    sealed class LoginEvent : UiEvent {
        data class SetAuthToken(val authTokenLoadState: LoadState) : LoginEvent()
        data class GetLogin(val loadState: LoadState) : LoginEvent()
        data class GetCheckUserRegistered(val isUserRegisteredState: LoadState) : LoginEvent()
        data class SetPagerState(val pagerState: PagerState) : LoginEvent()
    }
}
