package com.depromeet.team6.presentation.model.course

import androidx.compose.ui.graphics.Color

data class LegInfo(
    val transportType: TransportType,
    val subTypeIdx: Int = 0,
    val sectionTime: Int,
    val distance: Int,
    val routeColor: Color,
    val startPoint: WayPoint,
    val endPoint: WayPoint,
    val passShape : String
)
