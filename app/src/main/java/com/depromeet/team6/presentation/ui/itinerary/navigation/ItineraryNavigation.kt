package com.depromeet.team6.presentation.ui.itinerary.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.depromeet.team6.presentation.ui.itinerary.ItineraryRoute

fun NavController.navigateToItinerary(
    courseInfoJSON: String,
    departurePointJSON : String,
    destinationPointJSON : String
) {
    navigate(
        route = "${ItineraryRoute.ROUTE}/$courseInfoJSON/$departurePointJSON/$destinationPointJSON"
    ) {
        popUpTo(graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.itineraryNavGraph(
    padding: PaddingValues,
    navigateToBusCourse: (BusArrivalParameter) -> Unit,
    popBackStack: () -> Unit
) {
    composable(
        route = "${ItineraryRoute.ROUTE}/{courseInfoJSON}/{departurePointJSON}/{destinationPointJSON}",
        arguments = listOf(
            navArgument("courseInfoJSON") { type = NavType.StringType },
            navArgument("departurePointJSON") { type = NavType.StringType },
            navArgument("destinationPointJSON") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val courseInfo = backStackEntry.arguments?.getString("courseInfoJSON") ?: ""
        val departurePoint = backStackEntry.arguments?.getString("departurePointJSON") ?: ""
        val destinationPoint = backStackEntry.arguments?.getString("destinationPointJSON") ?: ""

        ItineraryRoute(
            padding = padding,
            courseInfoJSON = courseInfo,
            departurePointJSON = departurePoint,
            destinationPointJSON = destinationPoint,
            navigateToBusCourse = navigateToBusCourse,
            onBackPressed = popBackStack
        )
    }
}

object ItineraryRoute {
    const val ROUTE = "itinerary"
}
