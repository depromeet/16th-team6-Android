package com.depromeet.team6.presentation.ui.itinerary.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.model.course.LegInfo
import com.depromeet.team6.presentation.ui.course.component.CourseInfoDetail

@Composable
fun ItineraryDetail(
    legs : List<LegInfo>,
    modifier : Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(vertical = 12.dp)
    ) {
        CourseInfoDetail(
            legsInfo = legs
        )
    }

}