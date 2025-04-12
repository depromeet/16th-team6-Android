package com.depromeet.team6.presentation.ui.mypage

import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class MypageContract {
    data class MypageUiState(
        val loadState: LoadState = LoadState.Idle,
        val currentScreen: MypageScreen = MypageScreen.MAIN,
        val logoutState: Boolean = false,
        val isWebViewOpened: Boolean = false,
        val logoutDialogVisible: Boolean = false,
        val withDrawDialogVisible: Boolean = false
    ) : UiState

    enum class MypageScreen {
        MAIN, ACCOUNT, CHANGE_HOME
    }

    sealed interface MypageSideEffect : UiSideEffect {
        data object NavigateToLogin : MypageSideEffect
        data object NavigateBack : MypageSideEffect
    }

    sealed class MypageEvent : UiEvent {
        data object AccountClicked : MypageEvent()
        data object ChangeHomeClicked : MypageEvent()
        data object LogoutClicked : MypageEvent()
        data object WithDrawClicked : MypageEvent()
        data object BackPressed : MypageEvent()
        data object PolicyClicked : MypageEvent()
        data object PolicyClosed : MypageEvent()
        data object LogoutConfirmed : MypageEvent()
        data object WithDrawConfirmed : MypageEvent()
        data object DismissDialog : MypageEvent()
    }
}
