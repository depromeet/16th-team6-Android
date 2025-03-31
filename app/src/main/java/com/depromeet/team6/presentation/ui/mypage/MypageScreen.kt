package com.depromeet.team6.presentation.ui.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.common.view.AtChaWebView
import com.depromeet.team6.presentation.ui.mypage.component.MyPageConfirmDialog
import com.depromeet.team6.presentation.ui.mypage.component.MypageListItem
import com.depromeet.team6.presentation.ui.mypage.component.TitleBar
import com.depromeet.team6.presentation.util.WebViewUrl.PRIVACY_POLICY_URL
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.LocalTeam6Colors

@Composable
fun MypageRoute(
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(0.dp),
    mypageViewModel: MypageViewModel = hiltViewModel(),
    navigateBack: () -> Unit = {}
) {
    val uiState = mypageViewModel.uiState.collectAsStateWithLifecycle().value
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(mypageViewModel.sideEffect, lifecycleOwner) {
        mypageViewModel.sideEffect.flowWithLifecycle(lifecycle = lifecycleOwner.lifecycle)
            .collect { sideEffect ->
                when (sideEffect) {
                    is MypageContract.MypageSideEffect.NavigateBack -> navigateBack()
                    is MypageContract.MypageSideEffect.NavigateToLogin -> navigateToLogin()
                }
            }
    }

    when (uiState.loadState) {
        LoadState.Idle -> MypageScreen(
            modifier = modifier.padding(padding),
            mypageUiState = uiState,
            logoutClicked = { mypageViewModel.setEvent(MypageContract.MypageEvent.LogoutClicked) },
            withDrawClicked = { mypageViewModel.setEvent(MypageContract.MypageEvent.WithDrawClicked) },
            onBackClick = navigateBack,
            onWebViewClicked = { mypageViewModel.setEvent(MypageContract.MypageEvent.PolicyClicked) },
            webViewClose = { mypageViewModel.setEvent(MypageContract.MypageEvent.PolicyClosed) },
            logoutConfirmed = { mypageViewModel.setEvent(MypageContract.MypageEvent.LogoutConfirmed) },
            withDrawConfirmed = { mypageViewModel.setEvent(MypageContract.MypageEvent.WithDrawConfirmed) },
            dismissDialog = { mypageViewModel.setEvent(MypageContract.MypageEvent.DismissDialog) }
        )
        else -> Unit
    }
}

@Composable
fun MypageScreen(
    modifier: Modifier = Modifier,
    mypageUiState: MypageContract.MypageUiState = MypageContract.MypageUiState(),
    logoutClicked: () -> Unit = {},
    withDrawClicked: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onWebViewClicked: () -> Unit = {},
    webViewClose: () -> Unit = {},
    logoutConfirmed: () -> Unit = {},
    withDrawConfirmed: () -> Unit = {},
    dismissDialog: () -> Unit = {}
) {
    val colors = LocalTeam6Colors.current
    if (mypageUiState.isWebViewOpened) {
        AtChaWebView(url = PRIVACY_POLICY_URL, onClose = webViewClose, modifier = modifier)
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colors.greyWashBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .noRippleClickable { dismissDialog() }
            ) {
                TitleBar(
                    title = stringResource(R.string.mypage_title_text),
                    onBackClick = onBackClick
                )
                MypageListItem(
                    title = stringResource(R.string.mypage_account_logout_text),
                    onClick = logoutClicked
                )

                MypageListItem(
                    title = stringResource(R.string.mypage_account_signout_text),
                    onClick = withDrawClicked
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(colors.black)
                )

                MypageListItem(
                    title = stringResource(R.string.mypage_policy_text),
                    onClick = onWebViewClicked
                )
            }

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
}

@Preview
@Composable
fun MypageScreenPreview() {
    MypageScreen()
}
