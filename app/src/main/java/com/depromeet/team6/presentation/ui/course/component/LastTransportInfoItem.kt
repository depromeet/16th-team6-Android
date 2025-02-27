package com.depromeet.team6.presentation.ui.course.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.model.course.LastTransportInfo
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun LastTransportInfoItem(
    modifier : Modifier = Modifier,
    lastTransportInfo : LastTransportInfo
) {
    Column(
        modifier = modifier
            .background(defaultTeam6Colors.greyCard)
            .clip(RoundedCornerShape(12.dp))
    ) {
        // 남은 시간
        val remainingHour = lastTransportInfo.remainingMinutes / 60
        val remainingMinute = lastTransportInfo.remainingMinutes % 60
        Row {
            if (remainingHour > 0) {
                Text(
                    text = stringResource(id = R.string.last_transport_info_remaining_hour, remainingHour),
                    style = defaultTeam6Typography.heading4Medium20
                )
            }
            if (remainingMinute > 0) {
                Text(
                    text = stringResource(id = R.string.last_transport_info_remaining_minute, remainingMinute),
                    style = defaultTeam6Typography.heading4Medium20
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
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(text = "맻시")
            Text(text = stringResource(R.string.last_transport_info_departure_time))
            Text(text = "맻시")
            Text(text = stringResource(R.string.last_transport_info_boarding_time))
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )

        // 막차 경로 상세 정보

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )

        // 막차 알림 받기 버튼
    }
}



@Preview(name = "more than 1 hour")
@Composable
fun LastTransportInfoItemPreview(){
    val mockData = LastTransportInfo(
        remainingMinutes = 83
    )
    LastTransportInfoItem(
        lastTransportInfo = mockData
    )
}

@Preview(name = "less than 1 hour")
@Composable
fun LastTransportInfoItemPreview2(){
    val mockData = LastTransportInfo(
        remainingMinutes = 36
    )
    LastTransportInfoItem(
        lastTransportInfo = mockData
    )
}