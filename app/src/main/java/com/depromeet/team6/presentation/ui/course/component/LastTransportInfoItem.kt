package com.depromeet.team6.presentation.ui.course.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.model.course.LastTransportInfo
import com.depromeet.team6.presentation.model.course.TransportCourseInfo
import com.depromeet.team6.presentation.model.course.TransportType
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun LastTransportInfoItem(
    lastTransportInfo: LastTransportInfo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(defaultTeam6Colors.greyCard)
            .padding(16.dp)
    ) {
        // 남은 시간
        val remainingHour = lastTransportInfo.remainingMinutes / 60
        val remainingMinute = lastTransportInfo.remainingMinutes % 60
        Row {
            if (remainingHour > 0) {
                Text(
                    text = stringResource(
                        id = R.string.last_transport_info_remaining_hour, remainingHour
                    ),
                    style = defaultTeam6Typography.heading4Medium20,
                    color = defaultTeam6Colors.white
                )
            }
            if (remainingMinute > 0) {
                Text(
                    text = stringResource(
                        id = R.string.last_transport_info_remaining_minute, remainingMinute
                    ),
                    style = defaultTeam6Typography.heading4Medium20,
                    color = defaultTeam6Colors.white
                )
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )

        // 출발-탑승 상세 시각
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RemainingTimeHHmm(
                hour = lastTransportInfo.departureHour,
                minute = lastTransportInfo.departureMinute,
                isDeparture = true
            )
            Text(
                style = defaultTeam6Typography.bodyRegular13,
                color = defaultTeam6Colors.greySecondaryLabel,
                text = stringResource(R.string.last_transport_info_departure_time)
            )
            RemainingTimeHHmm(
                hour = lastTransportInfo.boardingHour,
                minute = lastTransportInfo.boardingMinute,
                isDeparture = false
            )
            Text(
                style = defaultTeam6Typography.bodyRegular13,
                color = defaultTeam6Colors.greySecondaryLabel,
                text = stringResource(R.string.last_transport_info_boarding_time)
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )

        // 막차 경로 상세 정보
        TransportCourseInfoExpandable(
            transportCourseInfo = lastTransportInfo.transportCourseInfo
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )

        // 막차 알림 받기 버튼
        SetNotificationButton()
    }
}

@Composable
fun SetNotificationButton(
    modifier : Modifier = Modifier
){
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color = Color(0x1F8AF265))
            .padding(vertical = 13.dp, horizontal = 28.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(16.dp),
            painter = painterResource(R.drawable.ic_all_alarm_clock_green),
            contentDescription = "set alarm icon",
        )
        Spacer(
            modifier = Modifier.width(4.dp)
        )
        Text(
            style = defaultTeam6Typography.bodyMedium14,
            color = defaultTeam6Colors.primaryMain,
            text = stringResource(R.string.last_transport_info_set_notification),
        )
    }
}

@Composable
fun RemainingTimeHHmm(
    hour : Int,
    minute : Int,
    isDeparture : Boolean,
    modifier : Modifier = Modifier
){
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(defaultTeam6Colors.systemGrey5)
    ) {
        val color = if (isDeparture) {
            defaultTeam6Colors.primaryMain
        } else {
            defaultTeam6Colors.white
        }
        Text(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp),
            color = color,
            text = stringResource(R.string.last_transport_info_remaining_time, hour, minute),
            style = defaultTeam6Typography.bodySemiBold12
        )
    }
}


@Preview(name = "more than 1 hour", showBackground = true, backgroundColor = android.graphics.Color.BLACK.toLong())
@Composable
fun LastTransportInfoItemPreview() {
    val courseInfo = listOf(
        TransportCourseInfo(
            type = TransportType.WALK,
            subTypeIdx = 0,
            durationMinutes = 10
        ),
        TransportCourseInfo(
            type = TransportType.BUS,
            subTypeIdx = 0,
            durationMinutes = 23
        ),
        TransportCourseInfo(
            type = TransportType.SUBWAY,
            subTypeIdx = 2,
            durationMinutes = 14
        )
    )

    val mockData = LastTransportInfo(
        remainingMinutes = 83,
        departureHour = 23,
        departureMinute = 3,
        boardingHour = 23,
        boardingMinute = 15,
        transportCourseInfo = courseInfo
    )
    LastTransportInfoItem(
        lastTransportInfo = mockData
    )
}

@Preview(name = "less than 1 hour", showBackground = true, backgroundColor = android.graphics.Color.BLACK.toLong())
@Composable
fun LastTransportInfoItemPreview2() {
    val courseInfo = listOf(
        TransportCourseInfo(
            type = TransportType.WALK,
            subTypeIdx = 0,
            durationMinutes = 10
        ),
        TransportCourseInfo(
            type = TransportType.BUS,
            subTypeIdx = 0,
            durationMinutes = 23
        ),
        TransportCourseInfo(
            type = TransportType.SUBWAY,
            subTypeIdx = 2,
            durationMinutes = 14
        )
    )

    val mockData = LastTransportInfo(
        remainingMinutes = 23,
        departureHour = 23,
        departureMinute = 3,
        boardingHour = 23,
        boardingMinute = 15,
        transportCourseInfo = courseInfo
    )
    LastTransportInfoItem(
        lastTransportInfo = mockData
    )
}