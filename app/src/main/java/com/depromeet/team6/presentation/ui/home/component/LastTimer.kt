package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.ui.theme.LocalTeam6Typography
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.max

@Composable
fun LastTimer(
    departureTime: String,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val typography = LocalTeam6Typography.current

    var remainingTimeText by remember { mutableStateOf("00:00") }

    LaunchedEffect(departureTime) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val targetTime = LocalTime.parse(departureTime, formatter)

        while (true) {
            val currentTime = LocalTime.now()

            val hoursDiff = ChronoUnit.HOURS.between(currentTime, targetTime)
            val minutesDiff = ChronoUnit.MINUTES.between(currentTime, targetTime) % 60

            val hours = max(0, hoursDiff)
            val minutes = max(0, minutesDiff)

            remainingTimeText = String.format("%02d:%02d", hours, minutes)

            if (hours <= 0 && minutes <= 0) {
                remainingTimeText = "00:00"
                break
            }

            delay(1000) // 1초 대기
        }
    }

    val timeParts = remainingTimeText.split(":")
    val hour = if (timeParts.isNotEmpty()) timeParts[0] else "00"
    val minute = if (timeParts.size > 1) timeParts[1] else "00"

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = hour,
            style = typography.extraBold44,
            color = textColor
        )

        Text(
            text = "분",
            style = typography.heading6Bold15,
            color = textColor,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, end = 12.dp)
        )

        Text(
            text = minute,
            style = typography.extraBold44,
            color = textColor
        )

        Text(
            text = "초",
            style = typography.heading6Bold15,
            color = textColor,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
    }
}

@Preview
@Composable
fun LastTimerPreview() {
    LastTimer(
        departureTime = "15:30:00",
        textColor = defaultTeam6Colors.systemRed
    )
}