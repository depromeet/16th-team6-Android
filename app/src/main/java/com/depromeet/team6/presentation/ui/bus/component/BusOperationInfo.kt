package com.depromeet.team6.presentation.ui.bus.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.BusOperationInfo
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.ui.common.TransportVectorIconComposable
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun BusOperationInfoView(
    busOperationInfo: BusOperationInfo,
    busNumber: String,
    busColor: Color,
    modifier: Modifier = Modifier,
    backButtonClicked: () -> Unit = {}
) {
    val horizontalModifier = Modifier.padding(horizontal = 16.dp)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = defaultTeam6Colors.greyElevatedBackground)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_left_grey),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(vertical = 18.dp, horizontal = 16.dp)
                    .noRippleClickable { backButtonClicked() }
                    .align(Alignment.CenterStart)
            )
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TransportVectorIconComposable(
                    type = TransportType.BUS,
                    color = busColor,
                    isMarker = false,
                    modifier = Modifier
                        .size(18.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = busNumber,
                    style = defaultTeam6Typography.heading5SemiBold17,
                    color = defaultTeam6Colors.white
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "운행지역",
            style = defaultTeam6Typography.bodyMedium15,
            color = defaultTeam6Colors.white,
            modifier = horizontalModifier
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = horizontalModifier) {
            Text(
                text = busOperationInfo.startStationName,
                style = defaultTeam6Typography.bodyRegular14,
                color = defaultTeam6Colors.white
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_left_right_12),
                tint = Color.Unspecified,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = busOperationInfo.endStationName,
                style = defaultTeam6Typography.bodyRegular14,
                color = defaultTeam6Colors.white
            )
        }
        if (busOperationInfo.serviceHours.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "운행시간",
                style = defaultTeam6Typography.bodyMedium15,
                color = defaultTeam6Colors.white,
                modifier = horizontalModifier
            )
        }
        busOperationInfo.serviceHours.forEach { serviceHour ->
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = horizontalModifier, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = serviceHour.dailyType,
                    style = defaultTeam6Typography.bodyRegular13,
                    color = defaultTeam6Colors.greySecondaryLabel
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "${serviceHour.startTime} ~ ${serviceHour.endTime}",
                    style = defaultTeam6Typography.bodyRegular14,
                    color = defaultTeam6Colors.white
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "배차간격",
            style = defaultTeam6Typography.bodyMedium15,
            color = defaultTeam6Colors.white,
            modifier = horizontalModifier
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = horizontalModifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            busOperationInfo.serviceHours.forEachIndexed { index, serviceHour ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = serviceHour.dailyType,
                        style = defaultTeam6Typography.bodyRegular13,
                        color = defaultTeam6Colors.greySecondaryLabel
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${serviceHour.term}분",
                        style = defaultTeam6Typography.bodyRegular14,
                        color = defaultTeam6Colors.white
                    )
                }
                if (index != busOperationInfo.serviceHours.lastIndex) {
                    Spacer(modifier = Modifier.width(15.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = horizontalModifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_bus_course_info_12),
                tint = defaultTeam6Colors.greySecondaryLabel,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "버스 정보는 운행상황 및 운수사의 정책에 따라 실제와 다를 수 있습니다.",
                style = defaultTeam6Typography.bodyRegular11,
                color = defaultTeam6Colors.greySecondaryLabel
            )
        }
    }
}

@Preview
@Composable
private fun BusOperationInfoPreview() {
    BusOperationInfoView(
        busOperationInfo = BusOperationInfo(
            startStationName = "복정역환승센터",
            endStationName = "노들역",
            serviceHours = emptyList()
        ),
        busNumber = "350",
        busColor = LocalTeam6Colors.current.busMainLine
    )
}
