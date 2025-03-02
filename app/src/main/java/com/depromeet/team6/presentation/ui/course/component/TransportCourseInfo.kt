package com.depromeet.team6.presentation.ui.course.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.model.course.TransportCourseInfo
import com.depromeet.team6.presentation.model.course.TransportType
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun TransportCourseInfoExpandable(
    transportCourseInfo : List<TransportCourseInfo>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(defaultTeam6Colors.systemGrey5)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                itemsIndexed(transportCourseInfo) { index, courseInfo ->
                    Image(
                        painter = painterResource(id = courseInfo.type.getTransportSubtypeResourceId(context, courseInfo.subTypeIdx)),
                        contentDescription = "${courseInfo.type.name} $index",
                        modifier = Modifier
                            .size(20.dp)
                    )

                    // 마지막 아이템이 아니면 화살표 추가
                    if (index < transportCourseInfo.lastIndex) {
                        Spacer(
                            modifier = Modifier
                                .width(4.dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_all_arrow_right_grey),
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
                    .clickable {
                        expanded = !expanded
                    },
                painter = painterResource(id = R.drawable.ic_all_arrow_down_grey),
                contentDescription = "arrow down",
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Here is the expandable content!")
                Text("You can put anything you like here.")
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun TransportCourseInfoPreview() {
    val data = listOf(
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
    TransportCourseInfoExpandable(
        transportCourseInfo = data
    )
}