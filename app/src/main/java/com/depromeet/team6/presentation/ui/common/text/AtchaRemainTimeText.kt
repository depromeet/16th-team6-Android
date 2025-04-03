package com.depromeet.team6.presentation.ui.common.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.depromeet.team6.domain.model.BusStatus
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import kotlinx.coroutines.delay

@Composable
fun AtChaRemainTimeText(remainSecond: Int,busStatus: BusStatus, modifier: Modifier = Modifier) {
    var timeLeft by remember { mutableIntStateOf(remainSecond) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    val displayText = if (busStatus == BusStatus.OPERATING) {
        formatRemainTime(timeLeft)
    } else {
        busStatus.string
    }

    Text(
        modifier = modifier,
        text = displayText,
        style = defaultTeam6Typography.bodyRegular13,
        color = defaultTeam6Colors.systemRed
    )
}

@Preview
@Composable
private fun AtChaRemainTimeTextPreview() {
    AtChaRemainTimeText(remainSecond = 400, busStatus = BusStatus.END)
}

fun formatRemainTime(seconds: Int): String {
    if (seconds < 60) {
        return "곧 도착"
    } else {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "${minutes}분 ${remainingSeconds}초"
    }
}
