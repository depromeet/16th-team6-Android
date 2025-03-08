package com.depromeet.team6.presentation.ui.main.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.depromeet.team6.presentation.ui.home.navigation.homeNavGraph
import com.depromeet.team6.presentation.ui.login.navigation.loginGraph
import com.depromeet.team6.presentation.ui.mypage.navigation.mypageNavGraph
import com.depromeet.team6.presentation.ui.onboarding.navigation.onboardingNavGraph

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    navigator: MainNavigator,
    padding: PaddingValues
) {
    NavHost(
        navController = navigator.navHostController,
        startDestination = navigator.startDestination
    ) {
        homeNavGraph(
            padding = padding,
            navigateToLogin = navigator::navigateToLogin,
            navigateToMypage = navigator::navigateToMypage
        )

        onboardingNavGraph(
            padding = padding,
            navigateToHome = navigator::navigateToHome
        )

        loginGraph(
            padding = padding,
            navigateToOnboarding = navigator::navigateToOnboarding,
            navigateToHome = navigator::navigateToHome
        )

        mypageNavGraph(
            padding = padding,
            navigateToLogin = navigator::navigateToLogin,
            popBackStack = navigator::popBackStack
        )

        val previousRoute = navigator.navHostController.previousBackStackEntry?.destination?.route ?: "Unknown"
    }
}
