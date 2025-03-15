package com.depromeet.team6.presentation.ui.coursesearch.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.depromeet.team6.presentation.ui.coursesearch.CourseSearchRoute

fun NavController.navigateCourseSearch(departure: String, destination: String) {
    navigate(
        route = "${CourseSearchRoute.ROUTE}/$departure/$destination"
    ) {
        popUpTo(graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.courseSearchNavGraph(
    padding: PaddingValues,
    navigateToHome: () -> Unit,
    navigateToItinerary: () -> Unit
) {
    composable(
        route = "${CourseSearchRoute.ROUTE}/{departure}/{destination}",
        arguments = listOf(
            navArgument("departure") { type = NavType.StringType },
            navArgument("destination") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val departure = backStackEntry.arguments?.getString("departure") ?: ""
        val destination = backStackEntry.arguments?.getString("destination") ?: ""

        CourseSearchRoute(
            padding = padding,
            navigateToItinerary = navigateToItinerary,
            navigateToHome = navigateToHome,
            departure = departure,
            destination = destination
        )
    }
}

object CourseSearchRoute {
    const val ROUTE = "courseSearch"
}
