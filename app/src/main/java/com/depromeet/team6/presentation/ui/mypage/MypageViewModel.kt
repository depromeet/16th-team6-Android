package com.depromeet.team6.presentation.ui.mypage

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.data.repositoryimpl.UserInfoRepositoryImpl
import com.depromeet.team6.domain.usecase.DeleteWithDrawUseCase
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
    private val deleteWithDrawUseCase: DeleteWithDrawUseCase
) : BaseViewModel<MypageContract.MypageUiState, MypageContract.MypageSideEffect, MypageContract.MypageEvent>() {
    override fun createInitialState(): MypageContract.MypageUiState = MypageContract.MypageUiState()

    override suspend fun handleEvent(event: MypageContract.MypageEvent) {
        when (event) {
            is MypageContract.MypageEvent.BackPressed -> navigateBack()
            is MypageContract.MypageEvent.LogoutClicked -> setState { copy(loadState = event.loadState) }
            is MypageContract.MypageEvent.WithDrawClicked -> setState { copy(loadState = event.loadState) }
            is MypageContract.MypageEvent.PolicyClicked -> setState { copy(isWebViewOpened = true) }
            is MypageContract.MypageEvent.PolicyClosed -> setState { copy(isWebViewOpened = false) }
        }
    }

    fun logout() {
        userInfoRepositoryImpl.setAccessToken(userInfoRepositoryImpl.getRefreshToken())
        viewModelScope.launch {
            if (postLogoutUseCase().isSuccessful) {
                setEvent(MypageContract.MypageEvent.LogoutClicked(loadState = LoadState.Error))
                userInfoRepositoryImpl.clear()
            } else {
                setEvent(MypageContract.MypageEvent.LogoutClicked(loadState = LoadState.Idle))
            }
        }
    }

    fun withDraw() {
        viewModelScope.launch {
            if (deleteWithDrawUseCase().isSuccessful) {
                userInfoRepositoryImpl.clear()
                setEvent(MypageContract.MypageEvent.WithDrawClicked(loadState = LoadState.Error))
            } else {
                setEvent(MypageContract.MypageEvent.WithDrawClicked(loadState = LoadState.Idle))
            }
        }
    }

    private fun navigateBack() {
        setSideEffect(MypageContract.MypageSideEffect.NavigateBack)
    }
}
