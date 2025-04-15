package com.depromeet.team6.presentation.ui.coursesearch.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider

@Composable
fun TransportCourseInfoExpandable(
    legsInfo: List<LegInfo>,
    modifier: Modifier = Modifier,
    onToggleDismissClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    if (!expanded) {
        CourseInfoSimple(
            legs = legsInfo,
            onClick = {
                expanded = true
            }
        )
    } else {
        CourseInfoDetail(
            legsInfo = legsInfo,
            onClick = {
                expanded = false
                onToggleDismissClick()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransportCourseInfoPreview(
    @PreviewParameter(LegInfoDummyProvider::class) courseInfo: List<LegInfo>
) {
    TransportCourseInfoExpandable(
        legsInfo = courseInfo
    )
}
