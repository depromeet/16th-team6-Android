package com.depromeet.team6.presentation.ui.home.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.presentation.model.itinerary.FocusedMarkerParameter
import com.depromeet.team6.presentation.ui.home.HomeRoute
import com.depromeet.team6.presentation.ui.home.navigation.HomeRoute.ARGUMENT
import com.depromeet.team6.presentation.ui.home.navigation.HomeRoute.ROUTE

fun NavController.navigationHome(afterOnboarding: Boolean = false) {
    navigate(
        route = "$ROUTE?$ARGUMENT=$afterOnboarding"
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
    navigateToItinerary: (String, String, String, FocusedMarkerParameter?) -> Unit,
    navigateToSearchLocation: (Address) -> Unit
) {
    composable(
        route = HomeRoute.ROUTE_WITH_ARGUMENT,
        arguments = listOf(
            navArgument(ARGUMENT) {
                type = NavType.BoolType
                defaultValue = false
            }
        )
    ) { backStackEntry ->
        val afterOnboarding = backStackEntry.arguments?.getBoolean(ARGUMENT) ?: false

        HomeRoute(
            padding = padding,
            afterOnboarding = afterOnboarding,
            navigateToMypage = navigateToMypage,
            navigateToLogin = navigateToLogin,
            navigateToCourseSearch = navigateToCourseSearch,
            navigateToItinerary = navigateToItinerary,
            navigateToSearchLocation = navigateToSearchLocation
        )
    }
}

object HomeRoute {
    const val ROUTE = "home"
    const val ARGUMENT = "afterOnboarding"
    const val ROUTE_WITH_ARGUMENT = "$ROUTE?$ARGUMENT={$ARGUMENT}"
}
