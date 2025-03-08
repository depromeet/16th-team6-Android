package com.depromeet.team6.presentation.ui.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.mypage.component.MypageListItem
import com.depromeet.team6.presentation.ui.mypage.component.TitleBar
import com.depromeet.team6.ui.theme.LocalTeam6Colors

@Composable
fun MypageAccountScreen(
    modifier: Modifier = Modifier
) {
    val colors = LocalTeam6Colors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.greyWashBackground)
    ) {
        Column {
            TitleBar(
                title = stringResource(R.string.mypage_account_title_text)
            )

            MypageListItem(
                title = stringResource(R.string.mypage_account_logout_text),
                onClick = { },
                modifier = Modifier
            )

            MypageListItem(
                title = stringResource(R.string.mypage_account_signout_text),
                onClick = { },
                modifier = Modifier
            )
        }
    }
}

@Preview
@Composable
fun MypageAccountScreenPreview() {
    MypageAccountScreen()
}
