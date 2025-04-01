package com.depromeet.team6.presentation.ui.itinerary.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun ItineraryInfoDetailLegs(
    modifier: Modifier = Modifier
) {
    DottedLineWithCircles()
}

@Composable
private fun DottedLineWithCircles(
    modifier: Modifier = Modifier,
    radius: Dp = 6.dp,
    lineLength: Dp = 20.dp
) {
    val dotColor: Color = defaultTeam6Colors.systemGrey3

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            Canvas(modifier = Modifier.size(radius * 2)) {
                drawCircle(color = dotColor)
            }
            if (index != 2) {
                Spacer(modifier = Modifier.width(lineLength))
            }
        }
    }
}

@Preview
@Composable
fun ItineraryInfoDetailLegsPreview() {
    ItineraryInfoDetailLegs()
}
