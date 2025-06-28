package com.depromeet.team6.presentation.model.route

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route
    data object Login : Route
    data object Back : Route

    // TODO:  data object Name : Route
}
