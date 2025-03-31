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
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.util.view.TransportTypeUiMapper
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun BusStationItem(
    busSubtypeIdx: Int,
    modifier: Modifier = Modifier
) {
    val stationName = "버스정류장"
    val stationNumber = 14502

    val subwaySubtypeIdx = 4
    val subwayLineIconRes = TransportTypeUiMapper.getIconResId(TransportType.SUBWAY, subwaySubtypeIdx)
    val busColor = TransportTypeUiMapper.getColor(TransportType.BUS, busSubtypeIdx)
    Row(
        modifier = modifier.fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center) {
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(color = busColor)
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_bus_station_check_14),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
        Column(modifier = Modifier.padding(top = 16.dp, start = 10.dp, bottom = 17.dp)) {
            Text(
                text = stationName,
                color = defaultTeam6Colors.white,
                style = defaultTeam6Typography.bodyMedium14
            )
            Spacer(modifier = Modifier.height(1.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stationNumber.toString(),
                    style = defaultTeam6Typography.bodyRegular13,
                    color = defaultTeam6Colors.greySecondaryLabel
                )
                Spacer(modifier = Modifier.width(6.dp))
                Spacer(
                    modifier = Modifier
                        .width(0.5.dp)
                        .height(10.dp)
                        .background(color = defaultTeam6Colors.greyTertiaryLabel)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = ImageVector.vectorResource(subwayLineIconRes),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun BusStationItemPreview() {
    BusStationItem(
        busSubtypeIdx = 1
    )
}
