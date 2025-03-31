package com.depromeet.team6.presentation.ui.common.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun AtChaLoadingView() {
    Box(
        modifier = Modifier.fillMaxSize().background(color = defaultTeam6Colors.greyWashBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Loading...", color = LocalTeam6Colors.current.white)
    }
}

@Preview
@Composable
fun AtChaLoadingViewPreview() {
    AtChaLoadingView()
}
