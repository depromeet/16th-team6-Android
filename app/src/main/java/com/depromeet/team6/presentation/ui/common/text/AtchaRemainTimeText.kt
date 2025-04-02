package com.depromeet.team6.presentation.ui.common.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun AtChaRemainTimeText(remainSecond: Int, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = formatRemainTime(remainSecond),
        style = defaultTeam6Typography.bodyRegular13,
        color = defaultTeam6Colors.systemRed
    )
}

@Preview
@Composable
private fun AtChaRemainTimeTextPreview() {
    AtChaRemainTimeText(remainSecond = 400)
}

fun formatRemainTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "${minutes}분 ${remainingSeconds}초"
}
