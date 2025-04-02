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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.BusCongestion
import com.depromeet.team6.domain.model.BusRouteStation
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.ui.common.text.AtChaRemainTimeText
import com.depromeet.team6.presentation.util.view.TransportTypeUiMapper
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun BusStationItem(
    busRouteStation: BusRouteStation,
    busSubtypeIdx: Int,
    modifier: Modifier = Modifier,
    isTurnPoint: Boolean = false,
    isCurrentStation: Boolean = false,
    busRemainTime: Pair<Int, Int>? = null
) {
    val busColor = TransportTypeUiMapper.getColor(TransportType.BUS, busSubtypeIdx)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center) {
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 66.dp)
                    .width(4.dp)
                    .background(color = busColor)
            )
            Icon(
                modifier = Modifier
                    .padding(start = 66.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_bus_station_check_14),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_bus_turn_point),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(start = 44.dp)
                    .alpha(if (isTurnPoint) 1f else 0f)
            )
            BusStatusIcon(
                busNumber = 5400.toString(), busCongestion = BusCongestion.LOW,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
        Column(modifier = Modifier.padding(top = 16.dp, start = 10.dp, bottom = 17.dp)) {
            Text(
                text = busRouteStation.busStationName,
                color = defaultTeam6Colors.white,
                style = defaultTeam6Typography.bodyMedium14
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = busRouteStation.busStationId,
                style = defaultTeam6Typography.bodyRegular13,
                color = defaultTeam6Colors.greySecondaryLabel
            )
            if (isCurrentStation && busRemainTime != null) {
                Spacer(modifier = Modifier.height(5.dp))
                AtChaRemainTimeText(remainSecond = busRemainTime.first)
                AtChaRemainTimeText(remainSecond = busRemainTime.second)
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
            busStationId = 14503.toString(),
            busStationName = "버스정류장 이름",
            busStationLat = 127.0,
            busStationLon = 37.0,
            order = 73
        ),
        busSubtypeIdx = 1,
        isCurrentStation = true,
        busRemainTime = Pair(400, 5000)
    )
}
