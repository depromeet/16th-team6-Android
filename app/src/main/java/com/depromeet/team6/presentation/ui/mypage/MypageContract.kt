package com.depromeet.team6.presentation.ui.mypage

import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class MypageContract {
    data class MypageUiState(
        val loadState: LoadState = LoadState.Idle,
        val logoutState: Boolean = false
    ) : UiState

    sealed interface MypageSideEffect : UiSideEffect {
        data object NavigateToLogin : MypageSideEffect
        data object NavigateBack : MypageSideEffect
    }

    sealed class MypageEvent : UiEvent {
        data class LogoutClicked(val loadState: LoadState) : MypageEvent()
        data class WithDrawClicked(val loadState: LoadState) : MypageEvent()
        data object BackPressed : MypageEvent()
    }
}
