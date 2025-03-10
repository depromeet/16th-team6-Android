package com.depromeet.team6.presentation.ui.alarm

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object NotificationTimeConstants {
    const val DEPARTURE_DATE_TIME_STRING = "2025-03-09 00:37:00"

    fun getDepartureTimeWithTodayDate(): String {
        val timeOnly = DEPARTURE_DATE_TIME_STRING.split(" ")[1]

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = dateFormat.format(Calendar.getInstance().time)

        return "$todayDate $timeOnly"
    }

    fun getOriginalDepartureTime(): String {
        return DEPARTURE_DATE_TIME_STRING
    }
}
