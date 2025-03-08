package com.depromeet.team6.presentation.ui.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.home.HomeContract
import com.depromeet.team6.presentation.ui.home.HomeViewModel
import com.depromeet.team6.presentation.ui.mypage.component.MypageListItem
import com.depromeet.team6.presentation.ui.mypage.component.TitleBar
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.LocalTeam6Colors

@Composable
fun MypageRoute(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
    mypageViewModel: MypageViewModel = hiltViewModel()
) {
    val homeUiState = homeViewModel.uiState.collectAsStateWithLifecycle().value
    val mypageUiState = mypageViewModel.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    MypageScreen(
        mypageUiState = mypageUiState,
        homeUiState = homeUiState,
        logoutClicked = { homeViewModel.logout() },
        withDrawClicked = { homeViewModel.withDraw() },
        modifier = modifier
    )
}

@Composable
fun MypageScreen(
    modifier: Modifier = Modifier,
    homeUiState: HomeContract.HomeUiState = HomeContract.HomeUiState(),
    mypageUiState: MypageContract.MypageUiState = MypageContract.MypageUiState(),
    logoutClicked: () -> Unit = {},
    withDrawClicked: () -> Unit = {}
) {
    val colors = LocalTeam6Colors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.greyWashBackground)
    ) {
        Column {
            TitleBar(
                title = stringResource(R.string.mypage_title_text)
            )
//
//            MypageListItem(
//                title = stringResource(R.string.mypage_account_title_text),
//                onClick = { },
//                modifier = Modifier
//            )
//
//            Spacer(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(10.dp)
//                    .background(colors.black))

            MypageListItem(
                title = stringResource(R.string.mypage_account_logout_text),
                onClick = logoutClicked,
                modifier = Modifier.noRippleClickable {
                    logoutClicked()
                }
            )

            MypageListItem(
                title = stringResource(R.string.mypage_account_signout_text),
                onClick = withDrawClicked,
                modifier = Modifier.noRippleClickable {
                    withDrawClicked()
                }
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(colors.black)
            )

            MypageListItem(
                title = stringResource(R.string.mypage_policy_text),
                onClick = { },
                modifier = Modifier
            )
//
//            MypageListItem(
//                title = stringResource(R.string.mypage_version_title_text),
//                onClick = { },
//                modifier = Modifier
//            )
        }
    }
}

@Preview
@Composable
fun MypageScreenPreview() {
    MypageScreen()
}
