package com.depromeet.team6.presentation.ui.login

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.presentation.type.LoginViewPagerType
import com.depromeet.team6.presentation.ui.common.view.AtChaLoadingView
import com.depromeet.team6.presentation.ui.common.view.AtChaTreeDotsLoadingView
import com.depromeet.team6.presentation.ui.login.component.LoginIndicator
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.Team6Theme
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.delay

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

@OptIn(ExperimentalPagerApi::class)
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
        while (true) {
            delay(4000L)

            val nextPage = (uiState.pagerState.currentPage + 1) % LoginViewPagerType.entries.size

            if (!uiState.pagerState.isScrollInProgress) {
                uiState.pagerState.animateScrollToPage(nextPage)
            }
        }
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
            LoadState.Success -> viewModel.getCheck()
            else -> Unit
        }
    }

    LaunchedEffect(uiState.isUserRegisteredState) {
        when (uiState.isUserRegisteredState) {
            LoadState.Success -> {
                viewModel.getLogin()
            }

            LoadState.Error -> {
                viewModel.setEvent(LoginContract.LoginEvent.SetLoadingState(isLoading = false))
                navigateToOnboarding()
            }

            else -> Unit
        }
    }

    when (uiState.loadState) {
        LoadState.Idle -> {
            Box(modifier = modifier.fillMaxSize()) {
                LoginScreen(
                    padding = padding,
                    uiState = uiState,
                    onLoginClicked = {
                        viewModel.setEvent(LoginContract.LoginEvent.SetLoadingState(isLoading = true))
                        setLayoutLoginKakaoClickListener(context = context, callback = callback)
                    },
                    modifier = modifier
                )
                if (uiState.isLoading) {
                    AtChaTreeDotsLoadingView(
                        isLoading = uiState.isLoading,
                        loadingText = "로그인 중",
                        modifier = Modifier
                            .padding(horizontal = 36.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }

        LoadState.Loading -> {
            AtChaLoadingView()
        }

        LoadState.Success -> navigateToHome()

        else -> Unit
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LoginScreen(
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    uiState: LoginContract.LoginUiState = LoginContract.LoginUiState(),
    onLoginClicked: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF121212),
                        Color(0xFF262626)
                    )
                )
            )
            .padding(padding)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 60.dp),
            verticalArrangement = Arrangement.Center
        ) {
            LoginIndicator(
                selectedIndex = uiState.pagerState.currentPage,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(30.dp))
            HorizontalPager(
                count = LoginViewPagerType.entries.size,
                state = uiState.pagerState,
                modifier = Modifier
                    .fillMaxWidth()
            ) { page ->
                val loginViewPagerType = LoginViewPagerType.entries[page]
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(loginViewPagerType.textRes),
                        style = defaultTeam6Typography.heading1_H1B22,
                        color = defaultTeam6Colors.white,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        modifier = Modifier
                            .fillMaxWidth(),
                        painter = painterResource(id = loginViewPagerType.imageRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        Box(
            modifier = Modifier
                .padding(start = 24.dp, top = 28.dp, end = 24.dp, bottom = 20.dp)
                .fillMaxWidth()
                .background(
                    color = defaultTeam6Colors.kakaoLoginButton,
                    shape = RoundedCornerShape(8.dp)
                )
                .noRippleClickable { onLoginClicked() }
                .semantics { contentDescription = "kakao_login_button" }
                .padding(horizontal = 22.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_login_kakao),
                    contentDescription = null,
                    modifier = Modifier
                )
                Text(
                    text = "카카오로 계속하기",
                    style = defaultTeam6Typography.body2_B2SB15,
                    modifier = Modifier
                )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
private fun LoginScreenPreview() {
    Team6Theme {
        LoginScreen(padding = PaddingValues(0.dp))
    }
}
