package com.depromeet.team6.presentation.ui.home.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.depromeet.team6.presentation.ui.home.HomeScreen

fun NavController.navigationHome() {
    navigate(
        route = HomeRoute.ROUTE
    ) {
        popUpTo(graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.homeNavGraph(
    padding: PaddingValues
) {
    composable(route = HomeRoute.ROUTE) {
        HomeScreen(padding = padding)
    }
}

object HomeRoute {
    const val ROUTE = "home"
}
