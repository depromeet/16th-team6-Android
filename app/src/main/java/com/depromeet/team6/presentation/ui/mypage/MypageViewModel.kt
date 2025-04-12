package com.depromeet.team6.presentation.ui.mypage

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.data.repositoryimpl.UserInfoRepositoryImpl
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.usecase.DeleteWithDrawUseCase
import com.depromeet.team6.domain.usecase.GetUserInfoUseCase
import com.depromeet.team6.domain.usecase.PostLogoutUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val userInfoRepositoryImpl: UserInfoRepositoryImpl,
    private val postLogoutUseCase: PostLogoutUseCase,
    private val deleteWithDrawUseCase: DeleteWithDrawUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
) : BaseViewModel<MypageContract.MypageUiState, MypageContract.MypageSideEffect, MypageContract.MypageEvent>() {
    override fun createInitialState(): MypageContract.MypageUiState = MypageContract.MypageUiState()

    override suspend fun handleEvent(event: MypageContract.MypageEvent) {
        when (event) {
            is MypageContract.MypageEvent.BackPressed -> navigateBack()
            is MypageContract.MypageEvent.LogoutClicked -> setState { copy(logoutDialogVisible = true, withDrawDialogVisible = false) }
            is MypageContract.MypageEvent.WithDrawClicked -> setState { copy(withDrawDialogVisible = true) }
            is MypageContract.MypageEvent.PolicyClicked -> setState { copy(isWebViewOpened = true) }
            is MypageContract.MypageEvent.PolicyClosed -> setState { copy(isWebViewOpened = false) }
            is MypageContract.MypageEvent.LogoutConfirmed -> logout()
            is MypageContract.MypageEvent.WithDrawConfirmed -> withDraw()
            is MypageContract.MypageEvent.DismissDialog -> setState { copy(logoutDialogVisible = false, withDrawDialogVisible = false) }
            is MypageContract.MypageEvent.AccountClicked -> navigateToAccount()
            is MypageContract.MypageEvent.ChangeHomeClicked -> navigateToChangeHome()
            is MypageContract.MypageEvent.UpdateMyAddress -> getUserInfo()
        }
    }

    fun getUserInfo() {
        viewModelScope.launch {
            getUserInfoUseCase().onSuccess { userInfo ->
                setState {
                    copy(
                        myAdress = Address(
                            name = userInfo.address,
                            lat = userInfo.userHome.latitude,
                            lon = userInfo.userHome.longitude,
                            address = userInfo.address
                        )
                    )
                }
            }
        }
    }

    private fun logout() {
        userInfoRepositoryImpl.setAccessToken(userInfoRepositoryImpl.getRefreshToken())
        viewModelScope.launch {
            if (postLogoutUseCase().isSuccessful) {
                setSideEffect(MypageContract.MypageSideEffect.NavigateToLogin)
                setState { copy(loadState = LoadState.Error) }
                userInfoRepositoryImpl.clear()
            } else {
                setEvent(MypageContract.MypageEvent.LogoutClicked)
            }
        }
    }

    private fun withDraw() {
        viewModelScope.launch {
            if (deleteWithDrawUseCase().isSuccessful) {
                userInfoRepositoryImpl.clear()
                setSideEffect(MypageContract.MypageSideEffect.NavigateToLogin)
            } else {
                setEvent(MypageContract.MypageEvent.WithDrawClicked)
            }
        }
    }

    private fun navigateToAccount() {
        setState { copy(currentScreen = MypageContract.MypageScreen.ACCOUNT) }
    }

    private fun navigateToChangeHome() {
        setState { copy(currentScreen = MypageContract.MypageScreen.CHANGE_HOME) }
    }

    private fun navigateBack() {
        val currentScreen = currentState.currentScreen
        if (currentScreen == MypageContract.MypageScreen.MAIN) {
            setSideEffect(MypageContract.MypageSideEffect.NavigateBack)
        } else if (currentScreen == MypageContract.MypageScreen.ACCOUNT) {
            setState { copy(currentScreen = MypageContract.MypageScreen.MAIN) }
        } else if (currentScreen == MypageContract.MypageScreen.CHANGE_HOME) {
            setState { copy(currentScreen = MypageContract.MypageScreen.MAIN) }
        }
    }
}
