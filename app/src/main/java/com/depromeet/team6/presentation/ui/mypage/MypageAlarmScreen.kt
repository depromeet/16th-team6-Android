package com.depromeet.team6.presentation.ui.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.mypage.component.MypageListItem
import com.depromeet.team6.presentation.ui.mypage.component.MypageMapView
import com.depromeet.team6.presentation.ui.mypage.component.MypageSelectedHome
import com.depromeet.team6.presentation.ui.mypage.component.SoundVibrateSelectView
import com.depromeet.team6.presentation.ui.mypage.component.TitleBar
import com.depromeet.team6.presentation.ui.onboarding.component.AlarmTime
import com.depromeet.team6.presentation.ui.onboarding.component.OnboardingAlarmSelector
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography
import com.google.android.gms.maps.model.LatLng

@Composable
fun MypageAlarmScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(0.dp),
    mypageUiState: MypageContract.MypageUiState = MypageContract.MypageUiState(),
    onBackClick: () -> Unit = {},
    dismissDialog: () -> Unit = {},
    onSoundSettingClick: () -> Unit = {},
    onAlarmTimeSettingClick: () -> Unit = {},
    onAlarmTypeSelected: (MypageContract.AlarmType) -> Unit = {}
) {
    val colors = LocalTeam6Colors.current
    val typography = LocalTeam6Typography.current
    val context = LocalContext.current

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
                title = stringResource(
                    when (mypageUiState.alarmScreenState) {
                        MypageContract.AlarmScreenState.MAIN -> R.string.mypage_alarm_title_text
                        MypageContract.AlarmScreenState.SOUND_SETTING -> R.string.mypage_alarm_sound_setting_text
                        else -> R.string.mypage_alarm_time_setting_text
                    }
                ),
                onBackClick = onBackClick
            )

            when (mypageUiState.alarmScreenState) {
                MypageContract.AlarmScreenState.MAIN -> {
                    MypageListItem(
                        title = stringResource(R.string.mypage_alarm_sound_setting_text),
                        onClick = onSoundSettingClick
                    )

                    MypageListItem(
                        title = stringResource(R.string.mypage_alarm_time_setting_text),
                        onClick = onAlarmTimeSettingClick
                    )
                }
                MypageContract.AlarmScreenState.SOUND_SETTING -> {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SoundVibrateSelectView(
                            type = MypageContract.AlarmType.SOUND,
                            isSelected = mypageUiState.selectedAlarmType == MypageContract.AlarmType.SOUND,
                            onSelected = {onAlarmTypeSelected(MypageContract.AlarmType.SOUND)},
                            modifier = Modifier.weight(1f)
                        )

                        SoundVibrateSelectView(
                            type = MypageContract.AlarmType.VIBRATION,
                            isSelected = mypageUiState.selectedAlarmType == MypageContract.AlarmType.VIBRATION,
                            onSelected = {onAlarmTypeSelected(MypageContract.AlarmType.VIBRATION)},
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                MypageContract.AlarmScreenState.TIME_SETTING -> {
                    Text(
                        text = stringResource(R.string.mypage_alarm_info_text),
                        style = typography.bodyRegular15,
                        color = colors.white,
                        modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp)
                    )
                    OnboardingAlarmSelector(
//                        selectedItems = uiState.alertFrequencies.mapNotNull { timeValue ->
//                            AlarmTime.entries.find { it.minutes == timeValue }
//                        }.toSet(),
//                        onItemClick = onAlarmTimeSelected
                        selectedItems = emptySet(),
                        onItemClick = {},
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }


        }

    }
}

@Preview
@Composable
fun MypageAlarmScreenPreview() {
    MypageAlarmScreen()
}