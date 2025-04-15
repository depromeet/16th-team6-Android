package com.depromeet.team6.presentation.ui.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.repository.UserInfoRepository
import com.depromeet.team6.domain.usecase.GetCheckUseCase
import com.depromeet.team6.domain.usecase.GetLoginUseCase
import com.depromeet.team6.presentation.util.Provider.KAKAO
import com.depromeet.team6.presentation.util.Token.BEARER
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalPagerApi::class)
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
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
            is LoginContract.LoginEvent.SetPagerState -> {
                setState { copy(pagerState = event.pagerState) }
            }
        }
    }

    fun setKakaoAccessToken(accessToken: String) {
        userInfoRepository.setAccessToken(accessToken)
        Timber.d("SetKakaoAccessToken= $accessToken")
        setEvent(LoginContract.LoginEvent.SetAuthToken(authTokenLoadState = LoadState.Success))
    }

    fun getLogin() {
        viewModelScope.launch {
            setEvent(LoginContract.LoginEvent.GetLogin(loadState = LoadState.Loading))
            getLoginUseCase(provider = KAKAO, fcmToken = userInfoRepository.getFcmToken()).onSuccess { auth ->
                setEvent(LoginContract.LoginEvent.GetLogin(loadState = LoadState.Success))
                userInfoRepository.setAccessToken(BEARER + auth.accessToken)
                userInfoRepository.setRefreshToken(auth.refreshToken)
                userInfoRepository.setUserHome(auth.userHome)
                userInfoRepository.setUserId(userId = auth.id)
            }.onFailure {
                setEvent(LoginContract.LoginEvent.GetLogin(loadState = LoadState.Error))
            }
        }
    }

    fun getCheck() {
        viewModelScope.launch {
            setEvent(LoginContract.LoginEvent.GetCheckUserRegistered(isUserRegisteredState = LoadState.Loading))
            getCheckUseCase(authorization = userInfoRepository.getAccessToken(), provider = KAKAO).onSuccess { isUserRegisteredState ->
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
        if (userInfoRepository.getRefreshToken()
            .isNotEmpty()
        ) {
            setEvent(LoginContract.LoginEvent.GetLogin(LoadState.Success))
        } else {
            Log.d("Login ViewModel", "Local Token is Empty")
        }
    }
}
