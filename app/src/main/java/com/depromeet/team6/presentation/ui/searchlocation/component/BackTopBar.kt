package com.depromeet.team6.presentation.ui.searchlocation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun BackTopBar(
    backButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = defaultTeam6Colors.greyElevatedBackground
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_left_white),
            contentDescription = stringResource(R.string.home_search_back_text),
            tint = defaultTeam6Colors.greyTertiaryLabel,
            modifier = Modifier
                .padding(18.dp)
                .noRippleClickable {
                    backButtonClick()
                }
        )
    }
}

@Preview
@Composable
fun BackTopBarPreview() {
    BackTopBar(
        backButtonClick = {}
    )
}