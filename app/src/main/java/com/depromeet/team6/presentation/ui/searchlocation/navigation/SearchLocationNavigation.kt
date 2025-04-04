package com.depromeet.team6.presentation.ui.searchlocation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.presentation.ui.searchlocation.SearchLocationRoute
import com.google.gson.Gson

fun NavController.navigationSearchLocation(
    destinationLocation : Address
) {
    val destinationLocationJSON = Gson().toJson(destinationLocation)
    navigate(
        route = "${SearchLocationRoute.ROUTE}/${destinationLocationJSON}"
    ) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.searchLocationNavigation(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToCourseSearch: (String, String) -> Unit,
) {
    composable(
        route = "${SearchLocationRoute.ROUTE}/{destinationLocationJSON}",
        arguments = listOf(
            navArgument("destinationLocationJSON") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val destinationLocationJSON = backStackEntry.arguments?.getString("destinationLocationJSON") ?: ""
        val destinationLocation = Gson().fromJson(destinationLocationJSON, Address::class.java)

        SearchLocationRoute(
            padding = padding,
            navigateToBack = navigateToBack,
            navigateToLogin = navigateToLogin,
            navigateToCourseSearch = navigateToCourseSearch,
            destinationLocation = destinationLocation
        )
    }
}

object SearchLocationRoute {
    const val ROUTE = "searchLocation"
}
