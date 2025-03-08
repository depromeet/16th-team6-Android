package com.depromeet.team6.presentation.ui.coursesearch.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.depromeet.team6.presentation.ui.coursesearch.CourseSearchRoute

fun NavController.navigateCourseSearch() {
    navigate(
        route = CourseSearchRoute.ROUTE
    ) {
        popUpTo(graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.courseSearchNavGraph(
    padding: PaddingValues,
) {
    composable(route = CourseSearchRoute.ROUTE) {
        CourseSearchRoute(
            padding = padding,
        )
    }
}

object CourseSearchRoute {
    const val ROUTE = "courseSearch"
}
