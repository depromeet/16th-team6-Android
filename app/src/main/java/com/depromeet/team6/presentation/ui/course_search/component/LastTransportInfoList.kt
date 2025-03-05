package com.depromeet.team6.presentation.ui.course_search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.model.course.LastTransportInfo
import com.depromeet.team6.presentation.model.course.TransportCourseInfo
import com.depromeet.team6.presentation.model.course.TransportType
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun LastTransportInfoList(
    listData: List<LastTransportInfo>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(defaultTeam6Colors.black)
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(listData.size) { index ->
                LastTransportInfoItem(
                    lastTransportInfo = listData[index]
                )
            }
        }
    }
}

@Preview
@Composable
fun LastTransportInfoListPreview() {
    val courseInfo = listOf(
        TransportCourseInfo(
            type = TransportType.WALK,
            subTypeIdx = 0,
            durationMinutes = 10
        ),
        TransportCourseInfo(
            type = TransportType.BUS,
            subTypeIdx = 0,
            durationMinutes = 23
        ),
        TransportCourseInfo(
            type = TransportType.SUBWAY,
            subTypeIdx = 2,
            durationMinutes = 14
        )
    )

    val mockData = LastTransportInfo(
        remainingMinutes = 23,
        departureHour = 23,
        departureMinute = 3,
        boardingHour = 23,
        boardingMinute = 15,
        transportCourseInfo = courseInfo
    )
    val mockDataList = listOf(
        mockData,
        mockData,
        mockData,
        mockData,
        mockData,
        mockData
    )
    LastTransportInfoList(
        listData = mockDataList
    )
}
