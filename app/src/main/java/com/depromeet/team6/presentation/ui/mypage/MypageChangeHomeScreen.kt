package com.depromeet.team6.presentation.ui.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.mypage.component.MypageSelectedHome
import com.depromeet.team6.presentation.ui.mypage.component.TitleBar
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.LocalTeam6Colors

@Composable
fun MypageChangeHomeScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(0.dp),
    onBackClick: () -> Unit = {},
    dismissDialog: () -> Unit = {}
) {
    val colors = LocalTeam6Colors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.greyWashBackground)
            .padding(padding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .noRippleClickable { dismissDialog() }
        ) {
            TitleBar(
                title = stringResource(R.string.mypage_change_home_title_text),
                onBackClick = onBackClick
            )

            MypageSelectedHome(
//                homeLocation = mypageUiState.location,
                homeLocation = "우리집"
            )
        }
    }
}
@Preview
@Composable
fun MypageChangeHomeScreenPreview() {
    MypageChangeHomeScreen()
}
