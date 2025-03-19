package com.depromeet.team6.presentation.ui.home.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.depromeet.team6.presentation.ui.home.HomeRoute

fun NavController.navigationHome() {
    navigate(
        route = HomeRoute.ROUTE
    ) {
        popUpTo(graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.homeNavGraph(
    padding: PaddingValues,
    navigateToLogin: () -> Unit,
    navigateToCourseSearch: (String, String) -> Unit,
    navigateToMypage: () -> Unit,
    navigateToItinerary: () -> Unit
) {
    composable(route = HomeRoute.ROUTE) {
        HomeRoute(
            padding = padding,
            navigateToMypage = navigateToMypage,
            navigateToLogin = navigateToLogin,
            navigateToCourseSearch = navigateToCourseSearch,
            navigateToItinerary = navigateToItinerary
        )
    }
}

object HomeRoute {
    const val ROUTE = "home"
}
