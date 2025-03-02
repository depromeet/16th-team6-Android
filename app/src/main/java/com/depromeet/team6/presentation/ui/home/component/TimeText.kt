package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.ui.theme.LocalTeam6Typography
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun TimeText(
    timeToLeave: String,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val typography = LocalTeam6Typography.current

    val timeParts = timeToLeave.split(":")
    val hour = if (timeParts.isNotEmpty()) timeParts[0] else ""
    val minute = if (timeParts.size > 1) timeParts[1] else ""

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = hour,
            style = typography.extraBold44,
            color = textColor
        )

        Text(
            text = "시",
            style = typography.heading6Bold15,
            color = textColor,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, end = 12.dp)
        )

        Text(
            text = minute,
            style = typography.extraBold44,
            color = textColor
        )

        Text(
            text = "분",
            style = typography.heading6Bold15,
            color = textColor,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

    }

}

@Preview
@Composable
fun TimeTextPreview() {
    TimeText(
        timeToLeave = "22:30:00",
        textColor = defaultTeam6Colors.white
    )
}