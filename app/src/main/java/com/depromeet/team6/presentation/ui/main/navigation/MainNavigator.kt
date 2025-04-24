package com.depromeet.team6.presentation.ui.main.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.depromeet.team6.presentation.model.itinerary.FocusedMarkerParameter
import com.depromeet.team6.presentation.ui.bus.navigation.navigationToBusCourse
import com.depromeet.team6.presentation.ui.coursesearch.navigation.navigateCourseSearch
import com.depromeet.team6.presentation.ui.home.navigation.HomeRoute
import com.depromeet.team6.presentation.ui.home.navigation.navigationHome
import com.depromeet.team6.presentation.ui.itinerary.navigation.navigateToItinerary
import com.depromeet.team6.presentation.ui.login.navigation.LoginRoute
import com.depromeet.team6.presentation.ui.login.navigation.navigationLogin
import com.depromeet.team6.presentation.ui.mypage.navigation.navigationMypage
import com.depromeet.team6.presentation.ui.onboarding.navigation.OnboardingRoute
import com.depromeet.team6.presentation.ui.onboarding.navigation.navigationOnboarding
import com.depromeet.team6.presentation.ui.searchlocation.navigation.navigationSearchLocation
import com.google.firebase.analytics.FirebaseAnalytics

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
        destination: String,
        fromLockScreen: Boolean = false
    ) {
        navHostController.navigateCourseSearch(
            departurePoint = departure,
            destinationPoint = destination,
            fromLockScreen = fromLockScreen
        )
    }

    fun navigateToItinerary(
        courseInfoJSON: String,
        departurePointJSON: String,
        destinationPointJSON: String,
        focusedMarkerParameter: FocusedMarkerParameter? = null
    ) {
        navHostController.navigateToItinerary(
            courseInfoJSON,
            departurePointJSON,
            destinationPointJSON,
            focusedMarkerParameter
        )
    }

    fun navigateToMypage() {
        navHostController.navigationMypage()
    }

    fun navigateToSearchLocation(destinationLocation: Address) {
        navHostController.navigationSearchLocation(destinationLocation = destinationLocation)
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
    navHostController: NavHostController = rememberNavController(),
    firebaseAnalytics: FirebaseAnalytics
): MainNavigator = remember(navHostController) {
    // Firebase Analytics 에 화면 이동 정보 로깅
    navHostController.addOnDestinationChangedListener { _, destination, _ ->
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, destination.route)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }
    MainNavigator(navHostController = navHostController)
}
