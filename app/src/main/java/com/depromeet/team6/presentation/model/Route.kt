package com.depromeet.team6.presentation.model

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route

    // TODO:  data object Name : Route
}
