package com.depromeet.team6.presentation.ui.common.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import kotlinx.coroutines.delay

@Composable
fun AtChaRemainTimeWithStationText(
    remainSecond: Int,
    remainingStations: Int,
    modifier: Modifier = Modifier
) {
    var timeLeft by remember(remainSecond) { mutableIntStateOf(remainSecond.coerceAtLeast(0)) }

    LaunchedEffect(remainSecond) {
        timeLeft = remainSecond.coerceAtLeast(0)
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    Text(
        modifier = modifier,
        text = "${formatRemainTime(timeLeft)} (${remainingStations}번째 전)",
        style = defaultTeam6Typography.detail1_R12,
        color = defaultTeam6Colors.systemRed
    )
}
