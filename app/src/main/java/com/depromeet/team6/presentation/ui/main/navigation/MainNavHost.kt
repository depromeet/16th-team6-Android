package com.depromeet.team6.presentation.ui.main.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.depromeet.team6.presentation.ui.bus.navigation.busCourseNavGraph
import com.depromeet.team6.presentation.ui.coursesearch.navigation.courseSearchNavGraph
import com.depromeet.team6.presentation.ui.home.navigation.homeNavGraph
import com.depromeet.team6.presentation.ui.itinerary.navigation.itineraryNavGraph
import com.depromeet.team6.presentation.ui.login.navigation.loginGraph
import com.depromeet.team6.presentation.ui.mypage.navigation.mypageNavGraph
import com.depromeet.team6.presentation.ui.onboarding.navigation.onboardingNavGraph
import com.depromeet.team6.presentation.ui.searchlocation.navigation.searchLocationNavigation

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    navigator: MainNavigator,
    padding: PaddingValues
) {
    NavHost(
        navController = navigator.navHostController,
        startDestination = navigator.startDestination,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500),
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            )
        },
        popEnterTransition = {
            EnterTransition.None
        }
    ) {
        homeNavGraph(
            padding = padding,
            navigateToLogin = navigator::navigateToLogin,
            navigateToCourseSearch = navigator::navigateToCourseSearch,
            navigateToMypage = navigator::navigateToMypage,
            navigateToItinerary = navigator::navigateToItinerary,
            navigateToSearchLocation = navigator::navigateToSearchLocation
        )

        onboardingNavGraph(
            padding = padding,
            navigateToHome = navigator::navigateToHome
        )

        loginGraph(
            padding = padding,
            navigateToOnboarding = navigator::navigateToOnboarding,
            navigateToHome = navigator::navigateToHome
        )

        mypageNavGraph(
            padding = padding,
            navigateToLogin = navigator::navigateToLogin,
            popBackStack = navigator::popBackStack
        )

        courseSearchNavGraph(
            padding = padding,
            navigateToItinerary = navigator::navigateToItinerary,
            navigateToHome = navigator::navigateToHome
        )

        itineraryNavGraph(
            padding = padding,
            popBackStack = navigator::popBackStack,
            navigateToBusCourse = navigator::navigateToBusCourse
        )

        searchLocationNavigation(
            padding = padding,
            navigateToBack = navigator::popBackStack,
            navigateToLogin = navigator::navigateToLogin,
            navigateToCourseSearch = navigator::navigateToCourseSearch
        )

        busCourseNavGraph(
            padding = padding,
            navigateToBackStack = navigator::popBackStack
        )

        val previousRoute = navigator.navHostController.previousBackStackEntry?.destination?.route ?: "Unknown"
    }
}
