package com.depromeet.team6.presentation.ui.bus.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.depromeet.team6.presentation.ui.bus.BusCourseRoute
import com.depromeet.team6.presentation.ui.onboarding.OnboardingRoute

fun NavController.navigationToBusCourse() {
    navigate(
        route = BusCourseRoute.ROUTE
    )
}

fun NavGraphBuilder.busCourseNavGraph(
    padding: PaddingValues,
    navigateToBackStack : () -> Unit = {}
) {
    composable(route = BusCourseRoute.ROUTE) {
        BusCourseRoute(
            padding = padding,
            navigateToBackStack = navigateToBackStack
        )
    }
}

object BusCourseRoute {
    const val ROUTE = "busCourse"
}
