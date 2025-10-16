package com.depromeet.team6.presentation.ui.splash

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.main.MainActivity
import com.depromeet.team6.presentation.ui.main.MainViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.Team6Theme

@Composable
fun SplashScreen(
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = LocalActivity.current as MainActivity
    val viewModel: MainViewModel = hiltViewModel(activity)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.splashState == LoadState.Success) {
        if (uiState.autoLogin) {
            navigateToHome()
        } else {
            navigateToLogin()
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Team6Theme.colors.black),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_splash_app_logo),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    SplashScreen(
        {},
        {}
    )
}
