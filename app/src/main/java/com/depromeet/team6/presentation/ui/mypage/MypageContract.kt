package com.depromeet.team6.presentation.ui.mypage

import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class MypageContract {
    data class MypageUiState(
        val loadState: LoadState = LoadState.Idle
    ) : UiState

    sealed interface MypageSideEffect : UiSideEffect {
        data object DummySideEffect : MypageSideEffect
    }

    sealed class MypageEvent : UiEvent {
        data object OnLogoutClick : MypageEvent()
        data object OnSignoutClick : MypageEvent()
    }
}
