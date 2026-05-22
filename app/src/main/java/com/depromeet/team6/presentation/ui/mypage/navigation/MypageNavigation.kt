package com.depromeet.team6.presentation.ui.mypage.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.depromeet.team6.presentation.ui.mypage.MyPageRoute

fun NavController.navigationMypage() {
    navigate(
        route = "${MypageRoute.ROUTE}?${MypageRoute.ARG_INITIAL_SCREEN}=${MypageRoute.Screen.MAIN.route}"
    ) {
        popUpTo(graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavController.navigationMypageChangeHome() {
    navigate(
        route = "${MypageRoute.ROUTE}?${MypageRoute.ARG_INITIAL_SCREEN}=${MypageRoute.Screen.CHANGE_HOME.route}"
    ) {
        popUpTo(graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.mypageNavGraph(
    padding: PaddingValues,
    navigateToLogin: () -> Unit,
    popBackStack: () -> Unit
) {
    composable(
        route = MypageRoute.ROUTE_WITH_ARGUMENT,
        arguments = listOf(
            navArgument(MypageRoute.ARG_INITIAL_SCREEN) {
                type = NavType.StringType
                defaultValue = MypageRoute.Screen.MAIN.route
            }
        )
    ) { backStackEntry ->
        val initialScreenRoute =
            backStackEntry.arguments?.getString(MypageRoute.ARG_INITIAL_SCREEN)
                ?: MypageRoute.Screen.MAIN.route

        MyPageRoute(
            padding = padding,
            navigateBack = popBackStack,
            navigateToLogin = navigateToLogin,
            initialScreenRoute = initialScreenRoute
        )
    }
}

object MypageRoute {
    const val ROUTE = "mypage"
    const val ARG_INITIAL_SCREEN = "initialScreen"
    const val ROUTE_WITH_ARGUMENT = "$ROUTE?$ARG_INITIAL_SCREEN={$ARG_INITIAL_SCREEN}"

    enum class Screen(val route: String) {
        MAIN("main"),
        CHANGE_HOME("change_home")
    }
}
