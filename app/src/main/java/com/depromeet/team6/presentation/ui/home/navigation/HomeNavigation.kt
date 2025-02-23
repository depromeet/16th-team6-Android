package com.depromeet.team6.presentation.ui.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.depromeet.team6.presentation.ui.home.HomeScreen
import com.depromeet.team6.presentation.ui.onboarding.OnboardingRoute

fun NavController.navigationHome() {
    navigate(
        route = HomeRoute.ROUTE
    )
}

fun NavGraphBuilder.homeNavGraph(
) {
    composable(route = HomeRoute.ROUTE) {
        HomeScreen()
    }
}

object HomeRoute {
    const val ROUTE = "home"
}
