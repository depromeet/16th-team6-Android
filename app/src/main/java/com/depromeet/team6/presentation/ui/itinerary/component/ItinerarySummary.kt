package com.depromeet.team6.presentation.ui.itinerary.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.model.course.LegInfo
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ItinerarySummary(
    totalTimeMinute: Int,
    boardingTime: String,
    legs: List<LegInfo>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val totalTimeHour = totalTimeMinute / 60
    Column {
        // 남은 시간
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (totalTimeHour > 0) {
                Text(
                    text = totalTimeHour.toString(),
                    style = defaultTeam6Typography.heading2Bold26,
                    fontSize = 28.sp,
                    color = defaultTeam6Colors.white
                )
                Text(
                    text = "시간",
                    style = defaultTeam6Typography.bodyMedium14,
                    fontSize = 24.sp,
                    color = defaultTeam6Colors.white
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = totalTimeMinute.toString(),
                style = defaultTeam6Typography.heading2Bold26,
                fontSize = 28.sp,
                color = defaultTeam6Colors.white
            )
            Text(
                text = "분",
                style = defaultTeam6Typography.bodyMedium14,
                fontSize = 24.sp,
                color = defaultTeam6Colors.white
            )
        }

        // 예상 도착, 출발 시간
        val (departHour, departMinute) = LocalDateTime
            .parse(boardingTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            .let { it.hour to it.minute }
        Text(
            text = context.getString(
                R.string.itinerary_summary_total_time,
                departHour,
                departMinute,
                (departHour + totalTimeHour + ((departMinute + totalTimeMinute) / 60)) % 24,
                (departMinute + totalTimeMinute) % 60
            ),
            style = defaultTeam6Typography.bodyRegular12,
            color = defaultTeam6Colors.greyTertiaryLabel
        )

        // 대중교통 정보 요약
        SummaryBarChart(
            modifier = Modifier
                .padding(vertical = 16.dp),
            legs = legs
        )

        // 구분선
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(17.dp)
                .padding(vertical = 8.dp)
                .background(Color(0x0AFFFFFF))
        )
    }
}

@Preview(name = "1시간 23분")
@Composable
fun ItinerarySummaryPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    ItinerarySummary(
        totalTimeMinute = 78,
        boardingTime = "2025-03-07 22:52:00",
        legs = legs
    )
}

@Preview(name = "13분")
@Composable
fun ItinerarySummaryPreviewShort(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    ItinerarySummary(
        totalTimeMinute = 13,
        boardingTime = "2025-03-07 22:52:00",
        legs = legs
    )
}
