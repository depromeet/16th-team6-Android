package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.ui.theme.LocalTeam6Typography
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import timber.log.Timber
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun LastTimer(
    departureTime: String,
    textColor: Color,
    modifier: Modifier = Modifier,
    onTimerFinished: () -> Unit = {}
) {
    val typography = LocalTeam6Typography.current

    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val currentOnTimerFinished by rememberUpdatedState(onTimerFinished)
    val currentDepartureTime by rememberUpdatedState(departureTime)

    val targetTimeMillis by remember {
        derivedStateOf {
            parseTargetTimeMillis(currentDepartureTime)
        }
    }

    val remainingTimeMillis by remember {
        derivedStateOf {
            if (targetTimeMillis == null) {
                0L
            } else {
                val remaining = targetTimeMillis!! - currentTime
                if (remaining < 0) 0L else remaining
            }
        }
    }

    val remainingMinutes = remember(remainingTimeMillis) { (remainingTimeMillis / 1000 / 60).toInt() }
    val remainingSeconds = remember(remainingTimeMillis) { ((remainingTimeMillis / 1000) % 60).toInt() }

    LaunchedEffect(remainingTimeMillis) {
        if (remainingTimeMillis == 0L && targetTimeMillis != null) {
            currentOnTimerFinished()
        }
    }

    LaunchedEffect(departureTime) {
        Timber.d("departureTime 변경됨: $departureTime, 계산된 targetTimeMillis: $targetTimeMillis")
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = String.format("%02d", remainingMinutes),
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
            text = String.format("%02d", remainingSeconds),
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

private fun parseTargetTimeMillis(timeString: String): Long? {
    return try {
        if (timeString.matches(Regex("\\d+"))) {
            val secondsToAdd = timeString.toLong()
            System.currentTimeMillis() + (secondsToAdd * 1000)
        } else {
            val targetDateTime = try {
                LocalDateTime.parse(timeString)
            } catch (e: DateTimeParseException) {
                try {
                    val timeFormatter = if (timeString.contains(":") && timeString.split(":").size == 3) {
                        DateTimeFormatter.ofPattern("HH:mm:ss")
                    } else {
                        DateTimeFormatter.ofPattern("HH:mm")
                    }

                    val targetTime = LocalTime.parse(timeString, timeFormatter)
                    val now = LocalDateTime.now()

                    val targetDate = if (targetTime.isBefore(now.toLocalTime())) {
                        now.toLocalDate().plusDays(1)
                    } else {
                        now.toLocalDate()
                    }

                    LocalDateTime.of(targetDate, targetTime)
                } catch (e: Exception) {
                    Timber.e("시간 파싱 오류: $timeString - ${e.message}")
                    return null
                }
            }

            val duration = Duration.between(LocalDateTime.now(), targetDateTime)
            if (duration.isNegative) {
                0L
            } else {
                System.currentTimeMillis() + duration.toMillis()
            }
        }
    } catch (e: Exception) {
        Timber.e("타이머 시간 변환 오류: ${e.message}")
        null
    }
}

@Preview
@Composable
fun LastTimerPreview() {
    LastTimer(
        departureTime = "2025-03-14T23:46",
        textColor = defaultTeam6Colors.systemRed
    )
}

@Preview
@Composable
fun LastTimerSecondsPreview() {
    LastTimer(
        departureTime = "300", // 5분
        textColor = defaultTeam6Colors.systemRed
    )
}
