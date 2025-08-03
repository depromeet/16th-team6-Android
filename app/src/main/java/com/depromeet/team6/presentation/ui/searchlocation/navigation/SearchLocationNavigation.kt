package com.depromeet.team6.presentation.ui.searchlocation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.presentation.ui.searchlocation.SearchLocationRoute
import com.depromeet.team6.presentation.ui.searchlocation.navigation.SearchLocationRoute.DEPARTURE_LOCATION
import com.depromeet.team6.presentation.ui.searchlocation.navigation.SearchLocationRoute.DESTINATION_LOCATION
import com.google.gson.Gson

fun NavController.navigationSearchLocation(
    destinationLocation: Address,
    departureLocation: Address? = null
) {
    val destinationLocationJSON = Gson().toJson(destinationLocation)
    val departureLocationJSON = departureLocation?.let { Gson().toJson(it) }

    val route = if (departureLocation != null) {
        "${SearchLocationRoute.ROUTE}/$destinationLocationJSON?${DEPARTURE_LOCATION}=$departureLocationJSON"
    } else {
        "${SearchLocationRoute.ROUTE}/$destinationLocationJSON"
    }

    navigate(
        route = route
    ) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.searchLocationNavigation(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToCourseSearch: (String, String) -> Unit
) {
    composable(
        route = "${SearchLocationRoute.ROUTE}/{$DESTINATION_LOCATION}?$DEPARTURE_LOCATION={$DEPARTURE_LOCATION}",
        arguments = listOf(
            navArgument("destinationLocationJSON") { type = NavType.StringType },
            navArgument("departureLocationJSON") {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            }
        )
    ) { backStackEntry ->
        val destinationLocationJSON = backStackEntry.arguments?.getString("destinationLocationJSON") ?: ""
        val destinationLocation = Gson().fromJson(destinationLocationJSON, Address::class.java)
        val departureLocationJSON = backStackEntry.arguments?.getString("departureLocationJSON")
        val departureLocation = departureLocationJSON?.let { Gson().fromJson(it, Address::class.java) }

        SearchLocationRoute(
            padding = padding,
            navigateToBack = navigateToBack,
            navigateToLogin = navigateToLogin,
            navigateToCourseSearch = navigateToCourseSearch,
            destinationLocation = destinationLocation,
            departureLocation = departureLocation
        )
    }
}

object SearchLocationRoute {
    const val ROUTE = "searchLocation"
    const val DESTINATION_LOCATION = "destinationLocationJSON"
    const val DEPARTURE_LOCATION = "departureLocationJSON"
}
