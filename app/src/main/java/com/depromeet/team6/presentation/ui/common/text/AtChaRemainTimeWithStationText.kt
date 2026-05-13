package com.depromeet.team6.presentation.ui.common.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun AtChaRemainTimeWithStationText(
    remainSecond: Int,
    remainingStations: Int,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = "${formatRemainTime(remainSecond)} (${remainingStations}번째 전)",
        style = defaultTeam6Typography.detail1_R12,
        color = defaultTeam6Colors.systemRed
    )
}

