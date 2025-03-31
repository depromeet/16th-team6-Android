package com.depromeet.team6.presentation.ui.bus.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.depromeet.team6.presentation.ui.bus.BusCourseRoute
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder

fun NavController.navigationToBusCourse(busArrivalParameter: BusArrivalParameter) {
    val param = BusArrivalParameter(
        routeName = busArrivalParameter.routeName,
        stationName = busArrivalParameter.stationName,
        lat = busArrivalParameter.lat,
        lon = busArrivalParameter.lon,
        subtypeIdx = busArrivalParameter.subtypeIdx
    )

    val encoded = URLEncoder.encode(Json.encodeToString(param), "UTF-8")
    this.navigate(route = "${BusCourseRoute.ROUTE}/$encoded")
}

fun NavGraphBuilder.busCourseNavGraph(
    padding: PaddingValues,
    navigateToBackStack: () -> Unit = {}
) {
    composable(
        route = BusCourseRoute.ROUTE_WITH_ARGUMENT,
        arguments = listOf(
            navArgument(BusCourseRoute.ARGUMENT) { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val encoded = backStackEntry.arguments?.getString(BusCourseRoute.ARGUMENT)
        val parameter = encoded?.let { BusCourseRoute.parse(it) }

        parameter?.let {
            BusCourseRoute(
                padding = padding,
                navigateToBackStack = navigateToBackStack,
                busArrivalParameter = it
            )
        }
    }
}

object BusCourseRoute {
    const val ROUTE = "busCourse"
    const val ARGUMENT = "busArrivalParam"
    const val ROUTE_WITH_ARGUMENT = "$ROUTE/{$ARGUMENT}"

    fun parse(encoded: String): BusArrivalParameter {
        val decoded = URLDecoder.decode(encoded, "UTF-8")
        return Json.decodeFromString(decoded)
    }
}
