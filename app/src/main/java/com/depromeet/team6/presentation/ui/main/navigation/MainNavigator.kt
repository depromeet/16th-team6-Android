package com.depromeet.team6.presentation.ui.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.depromeet.team6.presentation.ui.bus.navigation.navigationToBusCourse
import com.depromeet.team6.presentation.ui.coursesearch.navigation.CourseSearchRoute
import com.depromeet.team6.presentation.ui.coursesearch.navigation.navigateCourseSearch
import com.depromeet.team6.presentation.ui.home.navigation.HomeRoute
import com.depromeet.team6.presentation.ui.home.navigation.navigationHome
import com.depromeet.team6.presentation.ui.itinerary.navigation.ItineraryRoute
import com.depromeet.team6.presentation.ui.itinerary.navigation.navigateToItinerary
import com.depromeet.team6.presentation.ui.login.navigation.LoginRoute
import com.depromeet.team6.presentation.ui.login.navigation.navigationLogin
import com.depromeet.team6.presentation.ui.mypage.navigation.navigationMypage
import com.depromeet.team6.presentation.ui.onboarding.navigation.OnboardingRoute
import com.depromeet.team6.presentation.ui.onboarding.navigation.navigationOnboarding

class MainNavigator(
    val navHostController: NavHostController
) {

    val startDestination = LoginRoute.ROUTE

    fun navigateToOnboarding() {
        clearBackStackTo(OnboardingRoute.ROUTE)
        navHostController.navigationOnboarding()
    }

    fun navigateToHome() {
        clearBackStackTo(HomeRoute.ROUTE)
        navHostController.navigationHome()
    }

    fun navigateToLogin() {
        clearBackStackTo(LoginRoute.ROUTE)
        navHostController.navigationLogin()
    }

    fun navigateToCourseSearch(
        departure: String,
        destination: String
    ) {
        clearBackStackTo(CourseSearchRoute.ROUTE)
        navHostController.navigateCourseSearch(
            departurePoint = departure,
            destinationPoint = destination
        )
    }

    fun navigateToItinerary(
        courseInfoJSON: String
    ) {
        clearBackStackTo(ItineraryRoute.ROUTE)
        navHostController.navigateToItinerary(courseInfoJSON)
    }

    fun navigateToMypage() {
        navHostController.navigationMypage()
    }

    fun navigateToBusCourse(busArrivalParameter: BusArrivalParameter) {
        navHostController.navigationToBusCourse(busArrivalParameter = busArrivalParameter)
    }

    fun popBackStack() {
        if (navHostController.previousBackStackEntry != null) {
            navHostController.popBackStack()
        }
    }

    private fun clearBackStackTo(destination: String) {
        navHostController.popBackStack(
            route = destination,
            inclusive = false
        )
    }
}

@Composable
fun rememberMainNavigator(
    navHostController: NavHostController = rememberNavController()
): MainNavigator = remember(navHostController) {
    MainNavigator(navHostController = navHostController)
}
