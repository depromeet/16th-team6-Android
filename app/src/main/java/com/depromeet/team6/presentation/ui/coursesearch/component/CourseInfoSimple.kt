package com.depromeet.team6.presentation.ui.coursesearch.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.presentation.ui.itinerary.LegInfoDummyProvider
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.view.TransportTypeUiMapper
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun CourseInfoSimple(
    legs: List<LegInfo>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(defaultTeam6Colors.systemGrey5)
            .padding(start = 7.dp, end = 10.dp, top = 6.dp, bottom = 6.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .noRippleClickable {
                onClick()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(legs) { index, courseInfo ->
                Image(
                    modifier = Modifier
                        .size(26.dp),
                    imageVector = ImageVector.vectorResource(id = TransportTypeUiMapper.getIconResId(courseInfo.transportType, courseInfo.subTypeIdx)),
                    contentDescription = "transport course icon"
                )

                // 마지막 아이템이 아니면 화살표 추가
                if (index < legs.lastIndex) {
                    Spacer(
                        modifier = Modifier
                            .width(4.dp)
                    )
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_right_big),
                        contentDescription = "transport course divider",
                        modifier = Modifier
                            .size(12.dp)
                    )
                }
            }
        }

        Image(
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.CenterVertically),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_down_big),
            contentDescription = "arrow down"
        )
    }
}

@Preview
@Composable
fun CourseInfoSimplePreview(
    @PreviewParameter(LegInfoDummyProvider::class) courseInfo: List<LegInfo>
) {
    CourseInfoSimple(
        legs = courseInfo
    )
}
