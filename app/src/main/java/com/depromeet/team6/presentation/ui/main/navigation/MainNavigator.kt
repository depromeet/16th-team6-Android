package com.depromeet.team6.presentation.ui.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.depromeet.team6.presentation.ui.home.navigation.HomeRoute
import com.depromeet.team6.presentation.ui.home.navigation.navigationHome
import com.depromeet.team6.presentation.ui.login.navigation.LoginRoute
import com.depromeet.team6.presentation.ui.login.navigation.navigationLogin
import com.depromeet.team6.presentation.ui.mypage.navigation.navigationMypage
import com.depromeet.team6.presentation.ui.onboarding.navigation.navigationOnboarding

class MainNavigator(
    val navHostController: NavHostController
) {

    val startDestination = LoginRoute.ROUTE

    fun navigateToOnboarding() {
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

    fun navigateToMypage() {
        navHostController.navigationMypage()
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
