package com.depromeet.team6.presentation.ui.bus.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.BusCongestion
import com.depromeet.team6.domain.model.BusPosition
import com.depromeet.team6.domain.model.BusRouteStation
import com.depromeet.team6.domain.model.BusStatus
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.ui.common.text.AtChaRemainTimeText
import com.depromeet.team6.presentation.util.view.TransportTypeUiMapper
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun BusStationItem(
    busRouteStation: BusRouteStation,
    busSubtypeIdx: Int,
    busStatus: Pair<BusStatus, BusStatus>,
    modifier: Modifier = Modifier,
    isFirstStation: Boolean = false,
    isLastStation: Boolean = false,
    isTurnPoint: Boolean = false,
    afterTurnPoint: Boolean = false,
    isCurrentStation: Boolean = false,
    busRemainTime: Pair<Int, Int>? = null,
    busPosition: BusPosition? = null
) {
    val busColor = TransportTypeUiMapper.getColor(TransportType.BUS, busSubtypeIdx)
    val (topAlpha, bottomAlpha) = when {
        isFirstStation -> 0f to 1f
        isLastStation -> 0.3f to 0f
        isTurnPoint -> 1f to 0.3f
        afterTurnPoint -> 0.3f to 0.3f
        else -> 1f to 1f
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box() {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 71.dp)
                    .width(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .graphicsLayer { alpha = topAlpha }
                        .background(busColor)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .graphicsLayer { alpha = bottomAlpha }
                        .background(busColor)
                )
            }
            Icon(
                modifier = Modifier
                    .padding(start = 66.dp, end = 3.dp)
                    .align(Alignment.Center),
                imageVector = ImageVector.vectorResource(R.drawable.ic_bus_station_check_14),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_bus_turn_point),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(start = 41.dp)
                    .align(Alignment.Center)
                    .alpha(if (isTurnPoint) 1f else 0f)

            )
            if (busPosition != null) {
                val busProgress = ((busPosition.sectionProgress) + 0.5) % 1
                Column(modifier = Modifier.fillMaxHeight()) {
                    Spacer(modifier = Modifier.weight(busProgress.toFloat()))
                    BusStatusIcon(
                        busNumber = busPosition.vehicleNumber,
                        busCongestion = busPosition.busCongestion,
                        color = busColor,
                        modifier = Modifier
                            .padding(start = 7.dp)
                    )
                    Spacer(modifier = Modifier.weight(1 - busProgress.toFloat()))
                }
            }
        }
        Column(modifier = Modifier.padding(top = 16.dp, start = 10.dp, bottom = 17.dp)) {
            Text(
                text = busRouteStation.busStationName,
                color = defaultTeam6Colors.white,
                style = defaultTeam6Typography.bodyMedium14
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = busRouteStation.busStationNumber,
                style = defaultTeam6Typography.bodyRegular13,
                color = defaultTeam6Colors.greySecondaryLabel
            )
            if (isCurrentStation && busRemainTime != null) {
                Spacer(modifier = Modifier.height(5.dp))
                AtChaRemainTimeText(remainSecond = busRemainTime.first, busStatus = busStatus.first)
                AtChaRemainTimeText(remainSecond = busRemainTime.second, busStatus = busStatus.second)
            }
        }
    }
}

@Preview
@Composable
private fun BusStationItemPreview() {
    BusStationItem(
        busRouteStation = BusRouteStation(
            busRouteId = 14501.toString(),
            busRouteName = "버스정류장",
            busStationNumber = "23241",
            busStationId = 14503.toString(),
            busStationName = "버스정류장 이름",
            busStationLat = 127.0,
            busStationLon = 37.0,
            order = 73
        ),
        busStatus = Pair(BusStatus.WAITING, BusStatus.END),
        busSubtypeIdx = 1,
//        isTurnPoint = true,
        isCurrentStation = true,
        busRemainTime = Pair(400, 5000),
        busPosition = BusPosition(
            vehicleId = 123.toString(),
            sectionOrder = 2,
            vehicleNumber = 24002.toString(),
            sectionProgress = 0.9,
            busCongestion = BusCongestion.LOW
        )
    )
}
