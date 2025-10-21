package com.depromeet.team6.presentation.ui.mypage

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.type.ButtonSize
import com.depromeet.team6.presentation.type.ButtonType
import com.depromeet.team6.presentation.ui.common.button.AtchaCommonButton
import com.depromeet.team6.presentation.ui.common.list.TextListItemRadio
import com.depromeet.team6.presentation.ui.common.sound.VolumeBottomSheet
import com.depromeet.team6.presentation.ui.mypage.component.TitleBar
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
fun MypageAlarmScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(0.dp),
    mypageUiState: MypageContract.MypageUiState = MypageContract.MypageUiState(),
    onBackClick: () -> Unit = {},
    onAlarmTypeModified: (MypageContract.AlarmType) -> Unit = {},
    onAlarmVolumeModified: (Int) -> Unit = {}
) {
    val colors = LocalTeam6Colors.current
    val context = LocalContext.current
    val audio = remember(context) {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    val systemMax =
        remember { audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1) }
    val systemMin = remember(systemMax) {
        ceil(systemMax * 0.10f).toInt().coerceAtLeast(1)
    }

    var selectedMode by remember {
        mutableStateOf(mypageUiState.selectedAlarmType)
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
        ) {
            TitleBar(
                title = stringResource(R.string.mypage_alarm_title_text),
                onBackClick = onBackClick
            )

            TextListItemRadio(
                modifier = Modifier
                    .noRippleClickable{
                        selectedMode = MypageContract.AlarmType.ALL
                    },
                text = "소리/진동",
                isSelected = selectedMode == MypageContract.AlarmType.ALL
            )

            TextListItemRadio(
                modifier = Modifier
                    .noRippleClickable{
                        selectedMode = MypageContract.AlarmType.SOUND
                    },
                text = "소리",
                isSelected = selectedMode == MypageContract.AlarmType.SOUND
            )

            TextListItemRadio(
                modifier = Modifier
                    .noRippleClickable{
                        selectedMode = MypageContract.AlarmType.VIBRATION
                    },
                text = "진동",
                isSelected = selectedMode == MypageContract.AlarmType.VIBRATION
            )

        }

        if (selectedMode != MypageContract.AlarmType.VIBRATION) {
            VolumeBottomSheet (
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                currentVolume = mypageUiState.alarmVolume,
                onButtonClicked = { volume ->
                    val mapped = (volume / 100f * systemMax).roundToInt()
                    val systemVolume = mapped.coerceAtLeast(systemMin) // 최소 10%
//                    audio.setStreamVolume(AudioManager.STREAM_MUSIC, systemVolume, 0)

                    onAlarmTypeModified(selectedMode)
                    onAlarmVolumeModified(systemVolume)
                }
            )
        } else {
            AtchaCommonButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)
                    .align(Alignment.BottomCenter),
                buttonType = ButtonType.PRIMARY,
                buttonSize = ButtonSize.LARGE,
                buttonText = stringResource(R.string.volume_bottom_sheet_setting_btn_tv),
                onClick = {
                    onAlarmTypeModified(selectedMode)
                }
            )
        }
    }
}

@Preview
@Composable
fun MypageAlarmScreenPreview_1() {
    val uiState = MypageContract.MypageUiState()
        .copy(
            selectedAlarmType = MypageContract.AlarmType.VIBRATION
        )
    MypageAlarmScreen(
        mypageUiState = uiState,
    )
}

@Preview
@Composable
fun MypageAlarmScreenPreview_2() {
    MypageAlarmScreen()
}
