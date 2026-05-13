package com.depromeet.team6.presentation.ui.itinerary.component

import android.util.SparseArray
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.zIndex
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.RealTimeBusArrival
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.DefaultLatLng.DEFAULT_LAT
import com.depromeet.team6.presentation.util.DefaultLatLng.DEFAULT_LNG
import com.depromeet.team6.presentation.util.Dimens
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import com.google.android.gms.maps.model.LatLng

@Composable
fun ItineraryInfoDetail(
    currentLocation: LatLng,
    legs: List<LegInfo>,
    userDeparted: Boolean,
    busArrivalStatus: SparseArray<List<RealTimeBusArrival>>,
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
            currentLocation = currentLocation,
            userDeparted = userDeparted,
            legs = legs,
            onClickBusInfo = onClickBusInfo,
            busArrivalStatus = busArrivalStatus
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
            style = defaultTeam6Typography.detail1_R12,
            color = defaultTeam6Colors.gray300
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
    val timelineIconSize = Dimens.LegTimelineIconSize
    val markerYOffset = if (isDestination) 0.dp else 2.dp
    val timelineTimeIconGap = Dimens.LegTimelineTimeIconGap
    val timelineTimeSlotWidth = Dimens.LegTimelineTimeSlotWidth
    val timelineColumnWidth = timelineTimeSlotWidth + timelineTimeIconGap + timelineIconSize

    Row(
        modifier = modifier.zIndex(1f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .width(timelineColumnWidth)
                .height(Dimens.LegDetailVerticalLineWidth),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(timelineTimeSlotWidth)) {
                BoardingTime(
                    boardingDateTime = arrivalTime,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            Spacer(modifier = Modifier.width(timelineTimeIconGap))

            Image(
                modifier = Modifier
                    .offset(y = markerYOffset)
                    .size(timelineIconSize),
                imageVector = ImageVector.vectorResource(markerIconId),
                contentDescription = ""
            )
        }
        Spacer(
            modifier = Modifier.width(6.dp)
        )
        Text(
            modifier = Modifier,
            text = name,
            style = defaultTeam6Typography.body5_B5SB14,
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
        currentLocation = LatLng(DEFAULT_LAT, DEFAULT_LNG),
        legs = legs,
        userDeparted = true,
        departureTime = "2025-03-11T22:12:00",
        departureName = "중앙빌딩",
        arrivalTime = "2025-03-11T00:21:00",
        arrivalName = "우리집",
        busArrivalStatus = SparseArray()
    )
}
