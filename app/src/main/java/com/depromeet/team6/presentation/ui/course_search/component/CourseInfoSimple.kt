package com.depromeet.team6.presentation.ui.course_search.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.model.course.LegInfo

@Composable
fun CourseInfoSimple(
    legs: List<LegInfo>,
    modifier: Modifier = Modifier
){
    val context = LocalContext.current
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(legs) { index, courseInfo ->
            Image(
                imageVector = ImageVector.vectorResource(id = courseInfo.transportType.getTransportSubtypeResourceId(context, courseInfo.subTypeIdx)),
                contentDescription = "${courseInfo.transportType.name} $index",
                modifier = Modifier
                    .size(20.dp)
            )

            // 마지막 아이템이 아니면 화살표 추가
            if (index < legs.lastIndex) {
                Spacer(
                    modifier = Modifier
                        .width(4.dp)
                )
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_all_arrow_right_grey),
                    contentDescription = "transport course divider",
                    modifier = Modifier
                        .size(12.dp)
                )
            }
        }
    }
}