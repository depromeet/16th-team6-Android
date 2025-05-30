package com.depromeet.team6.presentation.ui.coursesearch.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.depromeet.team6.presentation.ui.coursesearch.CourseSearchRoute

fun NavController.navigateCourseSearch(departurePoint: String, destinationPoint: String, fromLockScreen: Boolean = false) {
    navigate(
        route = "${CourseSearchRoute.ROUTE}/$departurePoint/$destinationPoint"
    ) {
//        popUpTo(graph.startDestinationId) { inclusive = true }
//        launchSingleTop = true
    }
}

fun NavGraphBuilder.courseSearchNavGraph(
    padding: PaddingValues,
    navigateToHome: () -> Unit,
    navigateToItinerary: (String, String, String) -> Unit
) {
    composable(
        route = "${CourseSearchRoute.ROUTE}/{departurePoint}/{destinationPoint}",
        arguments = listOf(
            navArgument("departurePoint") { type = NavType.StringType },
            navArgument("destinationPoint") { type = NavType.StringType },
            navArgument("fromLockScreen") {
                type = NavType.BoolType
                defaultValue = false
            }
        )
    ) { backStackEntry ->
        val departurePoint = backStackEntry.arguments?.getString("departurePoint") ?: ""
        val destinationPoint = backStackEntry.arguments?.getString("destinationPoint") ?: ""
        val fromLockScreen = backStackEntry.arguments?.getBoolean("fromLockScreen") ?: false

        CourseSearchRoute(
            padding = padding,
            navigateToItinerary = navigateToItinerary,
            navigateToHome = navigateToHome,
            departurePoint = departurePoint,
            destinationPoint = destinationPoint,
            fromLockScreen = fromLockScreen
        )
    }
}

object CourseSearchRoute {
    const val ROUTE = "courseSearch"
}
