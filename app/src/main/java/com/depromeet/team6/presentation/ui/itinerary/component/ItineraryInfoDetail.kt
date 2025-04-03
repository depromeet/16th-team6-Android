package com.depromeet.team6.presentation.ui.itinerary.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.Dimens
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun ItineraryInfoDetail(
    legs: List<LegInfo>,
    departureTime: String,
    departureName: String,
    arrivalTime: String,
    arrivalName: String,
    modifier: Modifier = Modifier,
    onClickBusInfo: (BusArrivalParameter) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // 출발
        ItineraryInfoSuffix(
            name = departureName,
            arrivalTime = departureTime,
            isDestination = false
        )

        ItineraryInfoDetailLegs(
            legs = legs,
            onClickBusInfo = onClickBusInfo
        )

        // 도착
        ItineraryInfoSuffix(
            name = arrivalName,
            isDestination = true,
            arrivalTime = arrivalTime
        )

        // 막차 정보 출처
        Text(
            modifier = Modifier
                .padding(top = 72.dp)
                .align(Alignment.CenterHorizontally),
            text = stringResource(R.string.itinerary_info_legs_data_source),
            style = defaultTeam6Typography.bodyRegular12,
            color = defaultTeam6Colors.systemGrey1
        )
    }
}

@Composable
private fun ItineraryInfoSuffix(
    name: String,
    isDestination: Boolean,
    arrivalTime: String = "",
    modifier: Modifier = Modifier
) {
    val markerIconId = if (isDestination) R.drawable.map_marker_arrival else R.drawable.map_marker_departure
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.width(Dimens.LegDetailVerticalLineWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(36.dp),
                imageVector = ImageVector.vectorResource(markerIconId),
                contentDescription = ""
            )
            BoardingTime(
                boardingDateTime = arrivalTime,
                modifier = Modifier
            )
        }
        Spacer(
            modifier = Modifier.width(6.dp)
        )
        Text(
            modifier = Modifier,
            text = name,
            style = defaultTeam6Typography.bodySemiBold14,
            color = defaultTeam6Colors.white
        )
    }
}

@Preview
@Composable
fun ItineraryInfoDetailPreview(
    @PreviewParameter(LegInfoDummyProvider::class) legs: List<LegInfo>
) {
    ItineraryInfoDetail(
        legs = legs,
        departureTime = "2025-03-11T22:12:00",
        departureName = "중앙빌딩",
        arrivalTime = "2025-03-11T00:21:00",
        arrivalName = "우리집"
    )
}
