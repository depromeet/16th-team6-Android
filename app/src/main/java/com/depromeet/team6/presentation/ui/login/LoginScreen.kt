package com.depromeet.team6.presentation.ui.login

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.depromeet.team6.presentation.util.Login.PLATFORM
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.depromeet.team6.domain.model.Login
import com.depromeet.team6.presentation.ui.home.HomeScreen

fun setLayoutLoginKakaoClickListener(
    context: Context,
    callback: (OAuthToken?, Throwable?) -> Unit
) {
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
    } else {
        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
    }
}


@Composable
fun LoginRoute(
    modifier: Modifier=Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToOnboarding: () -> Unit,
    navigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val callback: (OAuthToken?, Throwable?) -> Unit = { oAuthToken, _ ->
        if (oAuthToken != null) {
            viewModel.setKakaoAccessToken(oAuthToken.accessToken)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkAutoLogin()
    }

    LaunchedEffect(viewModel.sideEffect, lifecycleOwner) {
        viewModel.sideEffect.flowWithLifecycle(lifecycle = lifecycleOwner.lifecycle)
            .collect { signInSideEffect ->
                when (signInSideEffect) {
                    is LoginContract.LoginSideEffect.NavigateToOnboarding -> navigateToOnboarding()
                    is LoginContract.LoginSideEffect.NavigateToHome -> navigateToHome()
                }
            }
    }

    LaunchedEffect(uiState.authTokenLoadState) {
        when (uiState.authTokenLoadState) {
            LoadState.Success -> {
                viewModel.postLogin(login = Login(PLATFORM))
            }
            else -> {
                Unit
            }
        }
    }

    when (uiState.loadState) {
        LoadState.Idle -> {
            LoginScreen(
                signInUiState = uiState,
                onSignInClicked = {
                    setLayoutLoginKakaoClickListener(context = context, callback = callback)
                },
                modifier=modifier
            )
        }

        LoadState.Loading -> Unit

        LoadState.Success -> navigateToHome()

        LoadState.Error -> navigateToOnboarding()
    }
}

@Composable
fun LoginScreen(
    signInUiState: LoginContract.LoginUiState = LoginContract.LoginUiState(),
    onSignInClicked: () -> Unit,
    modifier: Modifier = Modifier,
    ) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .roundedBackgroundWithPadding(
                    backgroundColor = defaultTeam6Colors.kakaoLoginButton,
                    cornerRadius = 8.dp,

                    )
                .noRippleClickable { onSignInClicked() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "카카오톡 로그인", modifier = Modifier.padding(vertical = 14.dp))
        }
        Spacer(Modifier.height(20.dp))
    }
}


@Preview
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        signInUiState = TODO(),
        onSignInClicked = TODO(),
        modifier = TODO()
    )
}
