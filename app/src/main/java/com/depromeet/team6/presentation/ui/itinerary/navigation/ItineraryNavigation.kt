package com.depromeet.team6.presentation.ui.itinerary.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.depromeet.team6.presentation.model.itinerary.FocusedMarkerParameter
import com.depromeet.team6.presentation.ui.itinerary.ItineraryRoute
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder

fun NavController.navigateToItinerary(
    courseInfoJSON: String,
    departurePointJSON: String,
    destinationPointJSON: String,
    param: FocusedMarkerParameter?
) {
    val paramEncoded =
        if (param != null) {
            URLEncoder.encode(Json.encodeToString(param), "UTF-8")
        } else {
            ""
        }

    navigate(
        route = "${ItineraryRoute.ROUTE}/$courseInfoJSON/$departurePointJSON/$destinationPointJSON/$paramEncoded"
    ) {
    }
}

fun NavGraphBuilder.itineraryNavGraph(
    padding: PaddingValues,
    navigateToBusCourse: (BusArrivalParameter) -> Unit,
    popBackStack: () -> Unit
) {
    composable(
        route = "${ItineraryRoute.ROUTE}/{courseInfoJSON}/{departurePointJSON}/{destinationPointJSON}/{paramEncoded}",
        arguments = listOf(
            navArgument("courseInfoJSON") { type = NavType.StringType },
            navArgument("departurePointJSON") { type = NavType.StringType },
            navArgument("destinationPointJSON") { type = NavType.StringType },
            navArgument("paramEncoded") {
                type = NavType.StringType
                defaultValue = "" // null 대신 빈 문자열로 들어왔을 때 대비
                nullable = true
            }
        )
    ) { backStackEntry ->
        val courseInfo = backStackEntry.arguments?.getString("courseInfoJSON") ?: ""
        val departurePoint = backStackEntry.arguments?.getString("departurePointJSON") ?: ""
        val destinationPoint = backStackEntry.arguments?.getString("destinationPointJSON") ?: ""
        val paramStr = backStackEntry.arguments?.getString("paramEncoded") ?: ""
        val markerParam = if (paramStr.isNotBlank()) {
            Json.decodeFromString<FocusedMarkerParameter>(URLDecoder.decode(paramStr, "UTF-8"))
        } else {
            null
        }

        ItineraryRoute(
            padding = padding,
            courseInfoJSON = courseInfo,
            departurePointJSON = departurePoint,
            destinationPointJSON = destinationPoint,
            navigateToBusCourse = navigateToBusCourse,
            onBackPressed = popBackStack,
            focusedMarkerParam = markerParam
        )
    }
}

object ItineraryRoute {
    const val ROUTE = "itinerary"
}
