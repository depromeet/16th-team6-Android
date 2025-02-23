package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.ui.theme.LocalTeam6Colors

@Composable
fun CurrentLocationSheet(
    currentLocation: String,
    destination: String,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalTeam6Colors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .height(220.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    colors.greyWashBackground,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .padding(start = 16.dp, end = 16.dp, top = 28.dp, bottom = 20.dp),
        ) {
            LocationText(
                text = currentLocation,
                textColor = colors.systemGreen,
                backgroundColor = colors.greyButton,
                onClick = {},
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(4.dp))

            LocationText(
                text = destination,
                textColor = colors.greyTertiaryLabel,
                backgroundColor = colors.greyWashBackground,
                onClick = {},
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                text = stringResource(R.string.home_search_button_text),
                onClick = onSearchClick,
                modifier = modifier
            )
        }
    }
}

@Preview
@Composable
fun CurrentLocationSheetPreview() {
    CurrentLocationSheet(
        currentLocation = "현위치",
        destination = "목적지",
        onSearchClick = { },
        modifier = Modifier
    )
}