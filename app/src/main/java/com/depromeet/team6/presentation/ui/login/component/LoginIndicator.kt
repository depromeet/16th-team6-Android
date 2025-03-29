package com.depromeet.team6.presentation.ui.login.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun LoginIndicator(selectedIndex: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        for (i in 0 until 3) {
            val color = if (i == selectedIndex) defaultTeam6Colors.main else defaultTeam6Colors.systemGrey1
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, CircleShape)
            )
        }
    }
}

@Preview
@Composable
private fun LoginIndicatorPreview() {
    LoginIndicator(1)
}
