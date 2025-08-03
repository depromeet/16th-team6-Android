package com.depromeet.team6.presentation.ui.mypage

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.depromeet.team6.BuildConfig
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.common.view.AtChaWebView
import com.depromeet.team6.presentation.ui.mypage.component.MyPageConfirmDialog
import com.depromeet.team6.presentation.ui.mypage.component.MypageListItem
import com.depromeet.team6.presentation.ui.mypage.component.MypageVersionItem
import com.depromeet.team6.presentation.ui.mypage.component.TitleBar
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingSearchPopup
import com.depromeet.team6.presentation.util.MyPageAmplitude.MYPAGE_BANNER_CLICKED
import com.depromeet.team6.presentation.util.WebViewUrl.FEEDBACK_FORM_URL
import com.depromeet.team6.presentation.util.WebViewUrl.PRIVACY_POLICY_URL
import com.depromeet.team6.presentation.util.amplitude.AmplitudeUtils
import com.depromeet.team6.presentation.util.base.ApiErrorSideEffect
import com.depromeet.team6.presentation.util.dialog.LocalDialogController
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography

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
    val context = LocalContext.current
    val dialogController = LocalDialogController.current

    val isInitialized = remember { mutableMapOf("initialized" to false) }

    val feedbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(FEEDBACK_FORM_URL))

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    BackHandler {
        mypageViewModel.setEvent(MypageContract.MypageEvent.BackPressed)
    }

    LaunchedEffect(mypageViewModel.sideEffect, lifecycleOwner) {
        mypageViewModel.sideEffect.flowWithLifecycle(lifecycle = lifecycleOwner.lifecycle)
            .collect { sideEffect ->
                when (sideEffect) {
                    is MypageContract.MypageSideEffect.NavigateBack -> navigateBack()
                    is MypageContract.MypageSideEffect.NavigateToLogin -> navigateToLogin()
                    is MypageContract.MypageSideEffect.NavigateToFeedbackForm -> {
                        context.startActivity(feedbackIntent)
                        AmplitudeUtils.trackEvent(
                            eventName = MYPAGE_BANNER_CLICKED
                        )
                    }
                    is ApiErrorSideEffect.ShowToastSideEffect -> {
                        Toast.makeText(context, sideEffect.toastMessage, Toast.LENGTH_SHORT).show()
                    }
                    is ApiErrorSideEffect.NavigateToLoginSideEffect -> {
                        navigateToLogin()
                    }
                    is MypageContract.MypageSideEffect.SettingDialog -> {
                        dialogController.showSystemSettingsDialog(
                            context = context,
                            message = context.getString(R.string.all_dialog_location_permission)
                        )
                    }
                    is MypageContract.MypageSideEffect.ClearPermissionData -> {
                        PermissionUtil.clearAllPermissionData(context)
                    }
                }
            }
    }

    LaunchedEffect(Unit) {
        if (!isInitialized["initialized"]!!) {
            mypageViewModel.getUserInfo()
            isInitialized["initialized"] = true
        }
    }

    if (uiState.searchPopupVisible) {
        OnboardingSearchPopup(
            context = context,
            searchLocations = uiState.searchLocations,
            padding = padding,
            searchText = uiState.searchText,
            onSearchTextChange = { newText ->
                mypageViewModel.setState {
                    copy(searchText = newText)
                }
                mypageViewModel.setEvent(MypageContract.MypageEvent.UpdateSearchText(text = newText))
            },
            onBackButtonClicked = {
                mypageViewModel.setEvent(MypageContract.MypageEvent.SearchPopUpBackPressed)
            },
            selectButtonClicked = { address ->
                mypageViewModel.setEvent(
                    MypageContract.MypageEvent.LocationSelectButtonClicked(
                        address
                    )
                )
                mypageViewModel.setEvent(
                    MypageContract.MypageEvent.ChangeMapViewVisible(
                        true
                    )
                )
            },
            onTextClearButtonClicked = {
                mypageViewModel.setEvent(MypageContract.MypageEvent.ClearText)
            },
            settingDialog = {
                mypageViewModel.setSideEffect(MypageContract.MypageSideEffect.SettingDialog)
            }
        )
    } else {
        when (uiState.loadState) {
            LoadState.Idle -> {
                if (uiState.isWebViewOpened) {
                    AtChaWebView(
                        url = PRIVACY_POLICY_URL,
                        onClose = { mypageViewModel.setEvent(MypageContract.MypageEvent.PolicyClosed) },
                        modifier = modifier
                    )
                } else {
                    when (uiState.currentScreen) {
                        MypageContract.MypageScreen.MAIN -> {
                            MypageScreen(
                                padding = padding,
                                modifier = modifier,
                                mypageUiState = uiState,
                                onAccountClick = { mypageViewModel.setEvent(MypageContract.MypageEvent.AccountClicked) },
                                onChangeHomeClick = { mypageViewModel.setEvent(MypageContract.MypageEvent.ChangeHomeClicked) },
                                onAlarmSettingClick = {
                                    mypageViewModel.setEvent(MypageContract.MypageEvent.AlarmSettingClicked)
                                    if (!PermissionUtil.hasNotificationPermission(context)) {
                                        dialogController.showSystemSettingsDialog(
                                            context = context,
                                            message = context.getString(R.string.all_dialog_notification_permission),
                                            onConfirm = {
                                                PermissionUtil.requestNotificationPermission(context, notificationPermissionLauncher)
                                            }
                                        )
                                    }
                                },
                                onBackClick = { mypageViewModel.setEvent(MypageContract.MypageEvent.BackPressed) },
                                onWebViewClicked = { mypageViewModel.setEvent(MypageContract.MypageEvent.PolicyClicked) },
                                dismissDialog = { mypageViewModel.setEvent(MypageContract.MypageEvent.DismissDialog) },
                                onUpdateClicked = {
                                    mypageViewModel.navigateToPlayStore(context)
                                },
                                onBannerClicked = { mypageViewModel.setSideEffect(MypageContract.MypageSideEffect.NavigateToFeedbackForm) },
                                isUpdateBtnVisible = uiState.userInfo.appVersion != ("v" + BuildConfig.VERSION_NAME)
                            )
                        }

                        MypageContract.MypageScreen.ACCOUNT -> {
                            MypageAccountScreen(
                                padding = padding,
                                modifier = modifier,
                                mypageUiState = uiState,
                                logoutClicked = { mypageViewModel.setEvent(MypageContract.MypageEvent.LogoutClicked) },
                                withDrawClicked = { mypageViewModel.setEvent(MypageContract.MypageEvent.WithDrawClicked) },
                                onBackClick = { mypageViewModel.setEvent(MypageContract.MypageEvent.BackPressed) },
                                logoutConfirmed = { mypageViewModel.setEvent(MypageContract.MypageEvent.LogoutConfirmed) },
                                withDrawConfirmed = { mypageViewModel.setEvent(MypageContract.MypageEvent.WithDrawConfirmed) },
                                dismissDialog = { mypageViewModel.setEvent(MypageContract.MypageEvent.DismissDialog) }
                            )
                        }

                        MypageContract.MypageScreen.CHANGE_HOME -> {
                            MypageChangeHomeScreen(
                                padding = padding,
                                modifier = modifier,
                                mypageUiState = uiState,
                                onBackClick = { mypageViewModel.setEvent(MypageContract.MypageEvent.BackPressed) },
                                onModifyHomeButtonClick = {
                                    mypageViewModel.setEvent(MypageContract.MypageEvent.ShowSearchPopup)
                                },
                                getCenterLocation = { mypageViewModel.getCenterLocation(it) },
                                clearAddress = {
                                    mypageViewModel.setEvent(MypageContract.MypageEvent.ClearAddress)
                                    mypageViewModel.setEvent(
                                        MypageContract.MypageEvent.ChangeMapViewVisible(
                                            mapViewVisible = false
                                        )
                                    )
                                },
                                mapViewSelectButtonClicked = {
                                    mypageViewModel.updateUserLocation(context)
                                    mypageViewModel.modifyUserAddress(context)
                                    mypageViewModel.setEvent(
                                        MypageContract.MypageEvent.ChangeMapViewVisible(
                                            false
                                        )
                                    )
                                }
                            )
                        }

                        MypageContract.MypageScreen.ALARM -> {
                            MypageAlarmScreen(
                                padding = padding,
                                modifier = modifier,
                                mypageUiState = uiState,
                                onBackClick = { mypageViewModel.setEvent(MypageContract.MypageEvent.BackPressed) },
                                onAlarmTypeSelected = { type ->
                                    mypageViewModel.setEvent(MypageContract.MypageEvent.AlarmTypeSelected(type))
                                },
                                onSoundSettingClick = { mypageViewModel.setEvent(MypageContract.MypageEvent.SoundSettingClicked) },
                                onAlarmTimeSettingClick = { mypageViewModel.setEvent(MypageContract.MypageEvent.TimeSettingClicked) },
                                onAlarmTimeSelected = { alarmTime ->
                                    val timeValue = alarmTime.minutes
                                    if (timeValue != 1) {
                                        val newSelection = if (timeValue in uiState.alertFrequencies) {
                                            uiState.alertFrequencies - timeValue
                                        } else {
                                            uiState.alertFrequencies + timeValue
                                        }
                                        mypageViewModel.setEvent(
                                            MypageContract.MypageEvent.UpdateAlertFrequencies(
                                                newSelection
                                            )
                                        )
                                    }
                                },
                                onAlarmTimeSubmitSelected = {
                                    mypageViewModel.modifyAlarmFrequencies(context)
                                }
                            )
                        }
                    }
                }
            }

            LoadState.Loading -> Unit
            LoadState.Success -> Unit
            LoadState.Error -> Unit
        }
    }
}

@Composable
fun MypageScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(0.dp),
    mypageUiState: MypageContract.MypageUiState = MypageContract.MypageUiState(),
    onAccountClick: () -> Unit = {},
    onChangeHomeClick: () -> Unit = {},
    onAlarmSettingClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onWebViewClicked: () -> Unit = {},
    webViewClose: () -> Unit = {},
    onUpdateClicked: () -> Unit = {},
    logoutConfirmed: () -> Unit = {},
    withDrawConfirmed: () -> Unit = {},
    dismissDialog: () -> Unit = {},
    onBannerClicked: () -> Unit = {},
    isUpdateBtnVisible: Boolean = false
) {
    val colors = LocalTeam6Colors.current
    val typography = LocalTeam6Typography.current

    if (mypageUiState.isWebViewOpened) {
        AtChaWebView(url = PRIVACY_POLICY_URL, onClose = webViewClose, modifier = modifier.padding(padding))
    } else {
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
                    title = stringResource(R.string.mypage_title_text),
                    onBackClick = onBackClick
                )
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_mypage_banner),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxWidth()
                        .aspectRatio(328f / 88f)
                        .noRippleClickable { onBannerClicked() }
                        .padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                MypageListItem(
                    title = stringResource(R.string.mypage_account_title_text),
                    onClick = onAccountClick
                )

                MypageListItem(
                    title = stringResource(R.string.mypage_change_home_title_text),
                    onClick = onChangeHomeClick
                )

                MypageListItem(
                    title = stringResource(R.string.mypage_alarm_title_text),
                    onClick = onAlarmSettingClick
                )

                MypageListItem(
                    title = stringResource(R.string.mypage_policy_text),
                    onClick = onWebViewClicked
                )

                MypageVersionItem(
                    title = stringResource(R.string.mypage_version_title_text),
                    onClick = onUpdateClicked,
                    updateBtnVisibility = isUpdateBtnVisible
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

            Text(
                text = stringResource(R.string.itinerary_info_legs_data_source),
                style = typography.bodyRegular12,
                color = colors.gray300,
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            )
        }
    }
}

@Preview
@Composable
fun MypageScreenPreview() {
    MypageScreen()
}
