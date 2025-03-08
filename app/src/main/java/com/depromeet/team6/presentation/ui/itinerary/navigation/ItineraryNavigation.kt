package com.depromeet.team6.presentation.ui.itinerary.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.depromeet.team6.presentation.ui.coursesearch.CourseSearchRoute

fun NavController.navigateItinerary() {
    navigate(
        route = ItineraryRoute.ROUTE
    ) {
        popUpTo(graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.itineraryNavGraph(
    padding: PaddingValues
) {
    composable(route = ItineraryRoute.ROUTE) {
        CourseSearchRoute(
            padding = padding
        )
    }
}

object ItineraryRoute {
    const val ROUTE = "itinerary"
}
