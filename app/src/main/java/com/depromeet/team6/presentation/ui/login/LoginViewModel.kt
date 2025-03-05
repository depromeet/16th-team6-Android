package com.depromeet.team6.presentation.ui.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.data.repositoryimpl.UserInfoRepositoryImpl
import com.depromeet.team6.domain.usecase.GetCheckUseCase
import com.depromeet.team6.domain.usecase.GetLoginUseCase
import com.depromeet.team6.presentation.util.Provider.KAKAO
import com.depromeet.team6.presentation.util.Token.BEARER
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userInfoRepositoryImpl: UserInfoRepositoryImpl,
    val getLoginUseCase: GetLoginUseCase,
    val getCheckUseCase: GetCheckUseCase
) : BaseViewModel<LoginContract.LoginUiState, LoginContract.LoginSideEffect, LoginContract.LoginEvent>() {
    override fun createInitialState(): LoginContract.LoginUiState = LoginContract.LoginUiState()

    override suspend fun handleEvent(event: LoginContract.LoginEvent) {
        when (event) {
            is LoginContract.LoginEvent.SetAuthToken -> setState { copy(authTokenLoadState = event.authTokenLoadState) }
            is LoginContract.LoginEvent.GetLogin -> setState { copy(loadState = event.loadState) }
            is LoginContract.LoginEvent.GetCheckUserRegistered -> setState {
                copy(
                    isUserRegisteredState = event.isUserRegisteredState
                )
            }
        }
    }

    fun setKakaoAccessToken(accessToken: String) {
        userInfoRepositoryImpl.setAccessToken(accessToken)
        Log.d("SetKakaoAccessToken", "accessToken= $accessToken")
        setEvent(LoginContract.LoginEvent.SetAuthToken(authTokenLoadState = LoadState.Success))
    }

    fun getLogin() {
        viewModelScope.launch {
            setEvent(LoginContract.LoginEvent.GetLogin(loadState = LoadState.Loading))
            getLoginUseCase(provider = KAKAO).onSuccess { auth ->
                setEvent(LoginContract.LoginEvent.GetLogin(loadState = LoadState.Success))
                userInfoRepositoryImpl.setAccessToken(BEARER + auth.accessToken)
                userInfoRepositoryImpl.setRefreshToken(auth.refreshToken)
            }.onFailure {
                setEvent(LoginContract.LoginEvent.GetLogin(loadState = LoadState.Error))
            }
        }
    }

    fun getCheck() {
        viewModelScope.launch {
            setEvent(LoginContract.LoginEvent.GetCheckUserRegistered(isUserRegisteredState = LoadState.Loading))
            getCheckUseCase(authorization = "Bearer " + userInfoRepositoryImpl.getAccessToken(), provider = KAKAO).onSuccess { isUserRegisteredState ->
                if (isUserRegisteredState) {
                    setEvent(LoginContract.LoginEvent.GetCheckUserRegistered(isUserRegisteredState = LoadState.Success))
                } else {
                    setEvent(LoginContract.LoginEvent.GetCheckUserRegistered(isUserRegisteredState = LoadState.Error))
                }
            }.onFailure {
                setEvent(LoginContract.LoginEvent.GetCheckUserRegistered(isUserRegisteredState = LoadState.Error))
            }
        }
    }

    fun checkAutoLogin() {
        if (userInfoRepositoryImpl.getRefreshToken()
            .isNotEmpty()
        ) {
            setEvent(LoginContract.LoginEvent.GetLogin(LoadState.Success))
        } else {
        }
    }
}
