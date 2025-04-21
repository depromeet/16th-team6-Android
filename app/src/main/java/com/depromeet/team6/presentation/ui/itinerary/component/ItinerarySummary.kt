package com.depromeet.team6.presentation.ui.itinerary.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import java.time.LocalDateTime

@Composable
fun ItinerarySummary(
    totalTimeMinute: Int,
    boardingTime: String,
    legs: List<LegInfo>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val durationHour = totalTimeMinute / 60
    val durationMinute = totalTimeMinute % 60

//    Text(
//        text = durationHour.toString(),
//        style = defaultTeam6Typography.heading2Bold26,
//        fontSize = 28.sp,
//        color = defaultTeam6Colors.white
//    )
    Column(
        modifier = modifier
            .background(defaultTeam6Colors.greyWashBackground)
    ) {
        // 남은 시간
        if (durationHour > 0) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                text = stringResource(R.string.itinerary_summary_duration_time, durationHour, durationMinute),
                style = defaultTeam6Typography.heading2Bold26,
                fontSize = 28.sp,
                color = defaultTeam6Colors.white
            )
        } else {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                text = stringResource(R.string.itinerary_summary_duration_minute, durationMinute),
                style = defaultTeam6Typography.heading2Bold26,
                fontSize = 28.sp,
                color = defaultTeam6Colors.white
            )
        }

        // 예상 도착, 출발 시간
        val (departHour, departMinute) = LocalDateTime
            .parse(boardingTime)
            .let { it.hour to it.minute }
        Text(
            text = context.getString(
                R.string.itinerary_summary_total_time,
                departHour,
                departMinute,
                (departHour + durationHour + ((departMinute + durationMinute) / 60)) % 24,
                (departMinute + durationMinute) % 60
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

//        // 구분선
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(8.dp)
//        )
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(1.dp)
//                .background(Color(0x0AFFFFFF))
//        )
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(22.dp)
//        )
    }
}

@Preview(name = "1시간 23분")
@Composable
fun ItinerarySummaryPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    ItinerarySummary(
        totalTimeMinute = 78,
        boardingTime = "2025-03-07T22:52:00",
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
        boardingTime = "2025-03-07T22:52:00",
        legs = legs
    )
}
