package com.depromeet.team6.presentation.ui.coursesearch.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.depromeet.team6.domain.model.Address
import kotlinx.serialization.Serializable
import com.depromeet.team6.presentation.ui.coursesearch.CourseSearchRoute as CourseSearchComposable

@Serializable
data class CourseSearchRoute(
    val departurePoint: String,
    val destinationPoint: String,
    val fromLockScreen: Boolean = false
)

fun NavController.navigateCourseSearch(
    departurePoint: String,
    destinationPoint: String,
    fromLockScreen: Boolean = false
) {
    navigate(CourseSearchRoute(departurePoint, destinationPoint, fromLockScreen))
}

fun NavGraphBuilder.courseSearchNavGraph(
    padding: PaddingValues,
    navigateToHome: () -> Unit,
    navigateToHomeAfterAlarmRegister: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToItinerary: (String, String, String) -> Unit,
    navigateToSearchLocation: (Address, Address) -> Unit,
    popBackStack: () -> Unit
) {
    composable<CourseSearchRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<CourseSearchRoute>()
        CourseSearchComposable(
            padding = padding,
            navigateToItinerary = navigateToItinerary,
            navigateToHome = navigateToHome,
            navigateToHomeAfterAlarmRegister = navigateToHomeAfterAlarmRegister,
            navigateToLogin = navigateToLogin,
            navigateToSearchLocation = navigateToSearchLocation,
            popBackStack = popBackStack,
            departurePoint = route.departurePoint,
            destinationPoint = route.destinationPoint,
            fromLockScreen = route.fromLockScreen
        )
    }
}
