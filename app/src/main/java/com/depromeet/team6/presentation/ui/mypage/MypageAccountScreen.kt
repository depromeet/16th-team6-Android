package com.depromeet.team6.presentation.ui.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.mypage.component.MyPageConfirmDialog
import com.depromeet.team6.presentation.ui.mypage.component.MypageListItem
import com.depromeet.team6.presentation.ui.mypage.component.TitleBar
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.LocalTeam6Colors

@Composable
fun MypageAccountScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(0.dp),
    mypageUiState: MypageContract.MypageUiState = MypageContract.MypageUiState(),
    logoutClicked: () -> Unit = {},
    withDrawClicked: () -> Unit = {},
    onBackClick: () -> Unit = {},
    logoutConfirmed: () -> Unit = {},
    withDrawConfirmed: () -> Unit = {},
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
                title = stringResource(R.string.mypage_account_title_text),
                onBackClick = onBackClick
            )

            MypageListItem(
                title = stringResource(R.string.mypage_account_logout_text),
                arrowVisible = false,
                onClick = logoutClicked
            )

            MypageListItem(
                title = stringResource(R.string.mypage_account_signout_text),
                arrowVisible = false,
                onClick = withDrawClicked
            )
        }

        // 다이얼로그 표시
        if (mypageUiState.logoutDialogVisible) {
            MyPageConfirmDialog(
                modifier = Modifier.align(Alignment.Center),
                title = stringResource(R.string.mypage_logout_dialog_title),
                confirmText = stringResource(R.string.mypage_logout_dialog_confirm),
                onDismiss = dismissDialog,
                onSuccess = logoutConfirmed
            )
        }

        if (mypageUiState.withDrawDialogVisible) {
            MyPageConfirmDialog(
                modifier = Modifier.align(Alignment.Center),
                title = stringResource(R.string.mypage_withdraw_dialog_title),
                confirmText = stringResource(R.string.mypage_withdraw_dialog_confirm),
                onDismiss = dismissDialog,
                onSuccess = withDrawConfirmed
            )
        }
    }
}

@Preview
@Composable
fun MypageAccountScreenPreview() {
    MypageAccountScreen()
}
