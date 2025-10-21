package com.depromeet.team6.presentation.ui.mypage

import androidx.activity.compose.BackHandler
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
import com.depromeet.team6.presentation.ui.mypage.component.MypageListItem
import com.depromeet.team6.presentation.ui.mypage.component.TitleBar
import com.depromeet.team6.presentation.util.dialog.LocalDialogController
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
    moveToAccount: () -> Unit = {},
    logoutConfirmed: () -> Unit = {},
    withDrawConfirmed: (String) -> Unit = {},
    dismissDialog: () -> Unit = {}
) {
    val colors = LocalTeam6Colors.current
    val dialogController = LocalDialogController.current

    BackHandler(enabled = mypageUiState.logoutDialogVisible) {
        dismissDialog()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.gray950)
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
            dialogController.showAtchaTwoButtonAlert(
                message = stringResource(R.string.mypage_logout_dialog_title),
                confirmButtonText = stringResource(R.string.mypage_logout_dialog_confirm),
                closeButtonText = stringResource(R.string.mypage_dialog_cancle),
                onConfirm = logoutConfirmed,
                onDismiss = dismissDialog
            )
        }

        if (mypageUiState.withDrawScreenVisible) {
            MyPageWithdrawSelectScreen(
                modifier = Modifier,
                moveToAccount = moveToAccount,
                withDrawConfirmed = withDrawConfirmed
            )
        }
    }
}

@Preview
@Composable
fun MypageAccountScreenPreview() {
    MypageAccountScreen()
}
