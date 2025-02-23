package com.depromeet.team6.presentation.ui.onboarding.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.depromeet.team6.presentation.ui.onboarding.OnboardingRoute

fun NavController.navigationOnboarding() {
    navigate(
        route = OnboardingRoute.ROUTE
    )
}

fun NavGraphBuilder.onboardingNavGraph(
    padding: PaddingValues,
    navigateToHome: () -> Unit
) {
    composable(route = OnboardingRoute.ROUTE) {
        OnboardingRoute(padding = padding, navigateToHome = navigateToHome)
    }
}

object OnboardingRoute {
    const val ROUTE = "onboarding"
}
