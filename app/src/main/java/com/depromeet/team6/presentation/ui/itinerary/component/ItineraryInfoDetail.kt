package com.depromeet.team6.presentation.ui.itinerary.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.Dimens
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import java.time.LocalDateTime

@Composable
fun ItineraryInfoDetail(
    legs : List<LegInfo>,
    departureTime : String,
    departureName : String,
    arrivalTime : String,
    arrivalName : String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        // 출발
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val departureDateTime = LocalDateTime.parse(departureTime)
            Text(
                modifier = Modifier
                    .width(Dimens.LegDepartureTimeWidth),
                text = stringResource(
                    R.string.last_transport_info_remaining_time,
                    departureDateTime.hour,
                    departureDateTime.minute
                ),
                style = defaultTeam6Typography.bodyMedium11,
                color = defaultTeam6Colors.greyTertiaryLabel
            )
            Image(
                modifier = Modifier
                    .size(36.dp),
                imageVector = ImageVector.vectorResource(R.drawable.map_marker_departure),
                contentDescription = ""
            )
            Spacer(
                modifier = Modifier.width(6.dp)
            )
            Text(
                modifier = Modifier,
                text = departureName,
                style = defaultTeam6Typography.bodySemiBold14,
                color = defaultTeam6Colors.white
            )
        }

        // 도착
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val arrivalDateTime = LocalDateTime.parse(arrivalTime)
            Text(
                modifier = Modifier
                    .width(Dimens.LegDepartureTimeWidth),
                text = stringResource(
                    R.string.last_transport_info_remaining_time,
                    arrivalDateTime.hour,
                    arrivalDateTime.minute
                ),
                style = defaultTeam6Typography.bodyMedium11,
                color = defaultTeam6Colors.greyTertiaryLabel
            )
            Image(
                modifier = Modifier
                    .size(36.dp),
                imageVector = ImageVector.vectorResource(R.drawable.map_marker_arrival),
                contentDescription = ""
            )
            Spacer(
                modifier = Modifier.width(6.dp)
            )
            Text(
                modifier = Modifier,
                text = arrivalName,
                style = defaultTeam6Typography.bodySemiBold14,
                color = defaultTeam6Colors.white
            )
        }
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
        arrivalTime = "2025-03-11T23:21:00",
        arrivalName = "우리집"
        )
}