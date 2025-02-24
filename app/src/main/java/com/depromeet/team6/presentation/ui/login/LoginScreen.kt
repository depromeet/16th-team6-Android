package com.depromeet.team6.presentation.ui.login

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

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
    padding: PaddingValues,
    modifier: Modifier = Modifier,
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
//                viewModel.postLogin(login = Login(PLATFORM))
                navigateToOnboarding()
            }

            else -> {
                Unit
            }
        }
    }

    when (uiState.loadState) {
        LoadState.Idle -> {
            LoginScreen(
                padding = padding,
                signInUiState = uiState,
                onSignInClicked = {
                    setLayoutLoginKakaoClickListener(context = context, callback = callback)
                },
                modifier = modifier
            )
        }

        LoadState.Loading -> Unit

        LoadState.Success -> {
            navigateToOnboarding()
        }

        LoadState.Error -> navigateToOnboarding()
    }
}

@Composable
fun LoginScreen(
    padding: PaddingValues,
    signInUiState: LoginContract.LoginUiState = LoginContract.LoginUiState(),
    onSignInClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(padding)
            .fillMaxSize()
            .background(color = defaultTeam6Colors.black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(256.dp))
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_login_logo),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.padding(start = 34.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .roundedBackgroundWithPadding(
                    backgroundColor = defaultTeam6Colors.kakaoLoginButton,
                    cornerRadius = 8.dp

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
    LoginScreen(padding = PaddingValues(0.dp))
}
