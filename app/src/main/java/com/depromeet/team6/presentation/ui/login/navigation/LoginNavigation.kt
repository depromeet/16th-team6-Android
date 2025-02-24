package com.depromeet.team6.presentation.ui.login.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.depromeet.team6.presentation.ui.login.LoginRoute

fun NavController.navigationLogin() {
    navigate(
        route = LoginRoute.ROUTE
    ) {
        popUpTo(graph.id) {
            inclusive = true
        }
    }
}

fun NavGraphBuilder.loginGraph(
    padding: PaddingValues,
    navigateToOnboarding: () -> Unit,
    navigateToHome: () -> Unit
) {
    composable(route = LoginRoute.ROUTE) {
        LoginRoute(
            padding = padding,
            navigateToOnboarding = navigateToOnboarding,
            navigateToHome = navigateToHome
        )
    }
}

object LoginRoute {
    const val ROUTE = "login"
}
