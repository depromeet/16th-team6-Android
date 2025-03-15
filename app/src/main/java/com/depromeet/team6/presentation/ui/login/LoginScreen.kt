package com.depromeet.team6.presentation.ui.login

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.presentation.type.LoginViewPagerType
import com.depromeet.team6.presentation.ui.login.component.LoginIndicator
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.modifier.roundedBackgroundWithPadding
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
        viewModel.checkAutoLogin()
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
                navigateToOnboarding()
            }

            else -> Unit
        }
    }

    when (uiState.loadState) {
        LoadState.Idle -> {
            LoginScreen(
                padding = padding,
                uiState = uiState,
                onSignInClicked = {
                    setLayoutLoginKakaoClickListener(context = context, callback = callback)
                },
                modifier = modifier
            )
        }

        LoadState.Success -> navigateToHome()

        else -> Unit
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LoginScreen(
    padding: PaddingValues,
    uiState: LoginContract.LoginUiState = LoginContract.LoginUiState(),
    onSignInClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(padding)
            .fillMaxSize()
            .paint(
                painter = BitmapPainter(ImageBitmap.imageResource(R.drawable.img_login_background)),
                contentScale = ContentScale.Crop
            )
    ) {
        HorizontalPager(
            count = LoginViewPagerType.entries.size,
            state = uiState.pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            val loginViewPagerType = LoginViewPagerType.entries[page]
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = loginViewPagerType.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(Modifier.height(99.dp))
                Text(
                    text = stringResource(loginViewPagerType.mainTextRes),
                    style = defaultTeam6Typography.heading2Bold26,
                    color = defaultTeam6Colors.white,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(loginViewPagerType.subTextRes),
                    style = defaultTeam6Typography.bodyRegular14,
                    color = defaultTeam6Colors.greySecondaryLabel
                )
            }
        }
        LoginIndicator(
            selectedIndex = uiState.pagerState.currentPage,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .fillMaxWidth()
                .roundedBackgroundWithPadding(
                    backgroundColor = defaultTeam6Colors.kakaoLoginButton,
                    cornerRadius = 8.dp
                )
                .noRippleClickable { onSignInClicked() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_login_kakao),
                contentDescription = null,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "카카오 계정으로 시작하기",
                style = defaultTeam6Typography.heading6Bold15
            )
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
