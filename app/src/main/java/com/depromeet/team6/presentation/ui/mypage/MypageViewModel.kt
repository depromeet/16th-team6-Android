package com.depromeet.team6.presentation.ui.mypage

import android.util.Log
import com.depromeet.team6.domain.usecase.DummyUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val mypageUseCase: DummyUseCase
) : BaseViewModel<MypageContract.MypageUiState, MypageContract.MypageSideEffect, MypageContract.MypageEvent>() {
    override fun createInitialState(): MypageContract.MypageUiState = MypageContract.MypageUiState()

    override suspend fun handleEvent(event: MypageContract.MypageEvent) {
        when (event) {
            MypageContract.MypageEvent.OnLogoutClick -> onLogoutClick()
            MypageContract.MypageEvent.OnSignoutClick -> onSignoutClick()
        }
    }
    
    fun onLogoutClick() {
        Log.d("LogOut", "onLogoutClick: ")
    }
    
    fun onSignoutClick() {
        Log.d("SignOut", "onSignoutClick: ")
    }

}