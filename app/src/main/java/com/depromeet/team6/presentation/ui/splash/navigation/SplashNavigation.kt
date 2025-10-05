package com.depromeet.team6.presentation.ui.splash.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.depromeet.team6.presentation.ui.home.navigation.HomeRoute.ROUTE
import com.depromeet.team6.presentation.ui.splash.SplashScreen

fun NavController.navigateToSplash() {
    navigate(
        route = ROUTE
    ) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.splashNavGraph(
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit
) {
    composable(route = SplashRoute.ROUTE) {
        SplashScreen(
            navigateToLogin = navigateToLogin,
            navigateToHome = navigateToHome
        )
    }
}

object SplashRoute {
    const val ROUTE = "splash"

}