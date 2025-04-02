package com.depromeet.team6.presentation.ui.itinerary.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import java.time.LocalDateTime

@Composable
fun ItineraryDetail(
    courseInfo: CourseInfo,
    departurePoint: Address,
    destinationPoint: Address,
    modifier: Modifier = Modifier
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
            modifier = Modifier
        )
    }
}
