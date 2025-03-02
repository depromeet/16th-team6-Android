package com.depromeet.team6.presentation.model.course

data class TransportCourseInfo(
    val type: TransportType,
    val subTypeIdx: Int,
    val durationMinutes: Int
)
