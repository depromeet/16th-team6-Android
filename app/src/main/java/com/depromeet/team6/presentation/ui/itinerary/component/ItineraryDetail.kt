package com.depromeet.team6.presentation.ui.itinerary.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import java.time.LocalDateTime

@Composable
fun ItineraryDetail(
    courseInfo: CourseInfo,
    departurePoint: Address,
    destinationPoint: Address,
    modifier: Modifier = Modifier,
    onClickBusInfo: (BusArrivalParameter) -> Unit = {}
) {
    val arrivalDateTime = LocalDateTime.parse(courseInfo.departureTime).plusSeconds(courseInfo.totalTime.toLong())

    Column(
        modifier = modifier
            .padding(vertical = 12.dp)
    ) {
        ItineraryInfoDetail(
            legs = courseInfo.legs,
            departureTime = courseInfo.departureTime,
            departureName = departurePoint.name,
            arrivalTime = arrivalDateTime.toString(),
            arrivalName = destinationPoint.name,
            modifier = Modifier,
            onClickBusInfo = onClickBusInfo
        )
    }
}
