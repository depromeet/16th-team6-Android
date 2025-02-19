package com.depromeet.team6.presentation.ui.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.data.repositoryimpl.UserInfoRepositoryImpl
import com.depromeet.team6.domain.model.Login
import com.depromeet.team6.domain.usecase.PostLoginUseCase
import com.depromeet.team6.presentation.util.Token.BEARER
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userInfoRepositoryImpl: UserInfoRepositoryImpl,
    val postLoginUseCase: PostLoginUseCase
) : BaseViewModel<LoginContract.LoginUiState, LoginContract.LoginSideEffect, LoginContract.LoginEvent>() {
    override fun createInitialState(): LoginContract.LoginUiState = LoginContract.LoginUiState()

    override suspend fun handleEvent(event: LoginContract.LoginEvent) {
        when (event) {
            is LoginContract.LoginEvent.PostLogin -> setState { copy(loadState=event.loadState) }
            is LoginContract.LoginEvent.SetAuthToken ->  setState { copy(authTokenLoadState = event.authTokenLoadState) }
        }
    }

    fun setKakaoAccessToken(accessToken: String) {
        userInfoRepositoryImpl.setAccessToken(accessToken)
        Log.d("SetKakaoAccessToken","accessToken= $accessToken")
        setEvent(LoginContract.LoginEvent.SetAuthToken(authTokenLoadState = LoadState.Success))
    }

    fun postLogin(login: Login) {
        viewModelScope.launch {
            setEvent(LoginContract.LoginEvent.PostLogin(loadState = LoadState.Loading))
            postLoginUseCase(authorization = userInfoRepositoryImpl.getAccessToken(), logIn = login).onSuccess { auth ->
                setEvent(LoginContract.LoginEvent.PostLogin(loadState = LoadState.Success))
                userInfoRepositoryImpl.setAccessToken(BEARER + auth.accessToken)
                userInfoRepositoryImpl.setRefreshToken(auth.refreshToken)
            }.onFailure {
                setEvent(LoginContract.LoginEvent.PostLogin(loadState = LoadState.Error))
            }
        }
    }

    fun checkAutoLogin() {
        if (userInfoRepositoryImpl.getRefreshToken().isNotEmpty()) setEvent(LoginContract.LoginEvent.PostLogin(LoadState.Success))
    }
}
