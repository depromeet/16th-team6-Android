package com.depromeet.team6.presentation.ui.mypage.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.depromeet.team6.presentation.ui.mypage.MypageRoute

fun NavController.navigationMypage() {
    navigate(
        route = MypageRoute.ROUTE
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
    composable(route = MypageRoute.ROUTE) {
        MypageRoute(
            padding = padding,
            navigateBack = popBackStack,
            navigateToLogin = navigateToLogin
        )
    }
}

object MypageRoute {
    const val ROUTE = "mypage"
}
