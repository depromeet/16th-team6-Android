package com.depromeet.team6.presentation.ui.bus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun BusCourseScreen(
    modifier: Modifier = Modifier,
    backButtonClicked: () -> Unit = {}
) {
    val busNumber = 350
    val busIconTint = defaultTeam6Colors.busMainLine
    val runningBusCount = 2

    val grey = defaultTeam6Colors.greySecondaryLabel
    val white = defaultTeam6Colors.white

    val fullText = stringResource(R.string.bus_course_running_bus_count, runningBusCount)
    val numberStart = fullText.indexOf(runningBusCount.toString())
    val numberEnd = numberStart + runningBusCount.toString().length

    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_left_grey),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(vertical = 18.dp, horizontal = 16.dp)
                    .align(Alignment.CenterStart)
            )
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_all_bus_18),
                    tint = busIconTint,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = busNumber.toString(),
                    style = defaultTeam6Typography.heading5SemiBold17,
                    color = defaultTeam6Colors.white
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.bus_course_info),
                style = defaultTeam6Typography.bodyRegular14,
                color = defaultTeam6Colors.white
            )
            Spacer(modifier = Modifier.width(3.dp))
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_bus_course_info_12),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = buildAnnotatedString {
                    append(fullText.substring(0, numberStart))
                    withStyle(style = SpanStyle(color = white)) {
                        append(fullText.substring(numberStart, numberEnd))
                    }
                    append(fullText.substring(numberEnd))
                },
                style = defaultTeam6Typography.bodyRegular14.copy(color = grey)
            )
        }
    }
    LazyColumn {  }
}

@Preview
@Composable
private fun BusCourseScreenPreview() {
    BusCourseScreen()
}
