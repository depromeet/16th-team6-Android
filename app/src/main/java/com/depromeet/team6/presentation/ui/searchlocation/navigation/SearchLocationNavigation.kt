package com.depromeet.team6.presentation.ui.searchlocation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.depromeet.team6.presentation.ui.searchlocation.SearchLocationRoute

fun NavController.navigationSearchLocation() {
    navigate(
        route = SearchLocationRoute.ROUTE
    ) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.searchLocationNavigation(
    navigateToBack: () -> Unit,
    navigateToLogin: () -> Unit
) {
    composable(route = SearchLocationRoute.ROUTE) {
        SearchLocationRoute(
            navigateToBack = navigateToBack,
            navigateToLogin = navigateToLogin
        )
    }
}

object SearchLocationRoute {
    const val ROUTE = "searchLocation"
}
