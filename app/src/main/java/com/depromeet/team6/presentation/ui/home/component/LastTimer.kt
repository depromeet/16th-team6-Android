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
import timber.log.Timber
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun LastTimer(
    departureTime: String, // "2025-03-14T23:46" 형식 또는 "HH:mm:ss" 형식
    textColor: Color,
    modifier: Modifier = Modifier,
    onTimerFinished: () -> Unit = {}
) {
    val typography = LocalTeam6Typography.current

    var remainingMinutes by remember { mutableStateOf(0) }
    var remainingSeconds by remember { mutableStateOf(0) }
    var isTimerFinished by remember { mutableStateOf(false) }

    LaunchedEffect(departureTime) {
        try {
            // 먼저 ISO 형식 (2025-03-14T23:46)으로 파싱 시도
            val targetDateTime = try {
                LocalDateTime.parse(departureTime)
            } catch (e: DateTimeParseException) {
                // ISO 형식이 아니면 HH:mm:ss 또는 HH:mm 형식으로 시도
                try {
                    val timeFormatter = if (departureTime.contains(":") && departureTime.split(":").size == 3) {
                        DateTimeFormatter.ofPattern("HH:mm:ss")
                    } else {
                        DateTimeFormatter.ofPattern("HH:mm")
                    }

                    val targetTime = LocalTime.parse(departureTime, timeFormatter)
                    val now = LocalDateTime.now()

                    // 현재 날짜에 목표 시간을 합쳐서 오늘 날짜의 목표 시간을 설정
                    LocalDateTime.of(now.toLocalDate(), targetTime)
                } catch (e: Exception) {
                    Timber.e("시간 파싱 오류: $departureTime - ${e.message}")
                    null
                }
            }

            if (targetDateTime != null) {
                while (!isTimerFinished) {
                    val now = LocalDateTime.now()
                    val duration = Duration.between(now, targetDateTime)

                    // 음수가 되지 않도록 처리
                    if (duration.isNegative) {
                        remainingMinutes = 0
                        remainingSeconds = 0
                        isTimerFinished = true
                        onTimerFinished()
                        break
                    }

                    val totalSeconds = duration.seconds
                    remainingMinutes = (totalSeconds / 60).toInt()
                    remainingSeconds = (totalSeconds % 60).toInt()

                    // 타이머가 0이 되면 완료 콜백 호출
                    if (remainingMinutes <= 0 && remainingSeconds <= 0) {
                        isTimerFinished = true
                        onTimerFinished()
                        break
                    }

                    delay(1000) // 1초마다 업데이트
                }
            } else {
                // 시간 파싱에 실패한 경우
                Timber.e("시간 형식 파싱 실패: $departureTime")
            }
        } catch (e: Exception) {
            Timber.e("타이머 오류: ${e.message}")
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

@Preview
@Composable
fun LastTimerPreview() {
    LastTimer(
        departureTime = "2025-03-14T23:46",
        textColor = defaultTeam6Colors.systemRed
    )
}