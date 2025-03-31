package com.depromeet.team6.presentation.ui.itinerary.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.depromeet.team6.presentation.ui.itinerary.ItineraryRoute

fun NavController.navigateToItinerary(
    courseInfoJSON: String
) {
    navigate(
        route = "${ItineraryRoute.ROUTE}/$courseInfoJSON"
    ) {
        popUpTo(graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.itineraryNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit
) {
    composable(
        route = "${ItineraryRoute.ROUTE}/{courseInfoJSON}",
        arguments = listOf(
            navArgument("courseInfoJSON") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val departurePoint = backStackEntry.arguments?.getString("courseInfoJSON") ?: ""

        ItineraryRoute(
            padding = padding,
            courseInfoJSON = departurePoint,
            onBackPressed = popBackStack
        )
    }
}

object ItineraryRoute {
    const val ROUTE = "itinerary"
}
