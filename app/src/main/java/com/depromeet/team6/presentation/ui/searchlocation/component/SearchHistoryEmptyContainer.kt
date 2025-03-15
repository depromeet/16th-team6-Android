package com.depromeet.team6.presentation.ui.searchlocation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.depromeet.team6.R
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun SearchHistoryEmptyContainer(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = defaultTeam6Colors.greyWashBackground
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.home_search_empty_history),
            style = defaultTeam6Typography.bodyRegular15,
            color = defaultTeam6Colors.greyTertiaryLabel
        )
    }
}

@Preview
@Composable
fun SearchHistoryEmptyContainerPreview() {
    SearchHistoryEmptyContainer()
}
