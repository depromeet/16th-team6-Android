package com.depromeet.team6.presentation.ui.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.depromeet.team6.presentation.ui.home.navigation.navigationHome
import com.depromeet.team6.presentation.ui.login.navigation.LoginRoute
import com.depromeet.team6.presentation.ui.onboarding.navigation.navigationOnboarding

class MainNavigator(
    val navHostController: NavHostController
) {

    val startDestination = LoginRoute.ROUTE

    fun navigateToOnboarding() {
        navHostController.navigationOnboarding()
    }

    fun navigateToHome(){
        navHostController.navigationHome()
    }

    private fun popBackStack() {
        navHostController.popBackStack()
    }
}

@Composable
fun rememberMainNavigator(
    navHostController: NavHostController = rememberNavController()
): MainNavigator = remember(navHostController) {
    MainNavigator(navHostController = navHostController)
}