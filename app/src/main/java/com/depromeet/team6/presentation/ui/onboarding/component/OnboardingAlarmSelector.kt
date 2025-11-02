package com.depromeet.team6.presentation.ui.onboarding.component

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
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
import com.depromeet.team6.presentation.ui.onboarding.OnboardingContract
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
fun OnboardingAlarmSelector(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(0.dp),
    onAlarmSelected: (OnboardingContract.AlarmType, Int) -> Unit = { a, b -> }
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
        mutableStateOf(OnboardingContract.AlarmType.ALL)
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
            Spacer(modifier = Modifier.height(72.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                text = stringResource(R.string.onboarding_alarm_setting),
                style = defaultTeam6Typography.heading1_H1B22,
                color = defaultTeam6Colors.white
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextListItemRadio(
                modifier = Modifier
                    .noRippleClickable {
                        selectedMode = OnboardingContract.AlarmType.ALL
                    },
                text = "소리/진동",
                isSelected = selectedMode == OnboardingContract.AlarmType.ALL
            )

            TextListItemRadio(
                modifier = Modifier
                    .noRippleClickable {
                        selectedMode = OnboardingContract.AlarmType.SOUND
                    },
                text = "소리",
                isSelected = selectedMode == OnboardingContract.AlarmType.SOUND
            )

            TextListItemRadio(
                modifier = Modifier
                    .noRippleClickable {
                        selectedMode = OnboardingContract.AlarmType.VIBRATION
                    },
                text = "진동",
                isSelected = selectedMode == OnboardingContract.AlarmType.VIBRATION
            )
        }

        if (selectedMode != OnboardingContract.AlarmType.VIBRATION) {
            VolumeBottomSheet(
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                currentVolume = systemMax / 2,
                onButtonClicked = { volume ->
                    val mapped = (volume / 100f * systemMax).roundToInt()
                    val systemVolume = mapped.coerceAtLeast(systemMin) // 최소 10%

                    onAlarmSelected(selectedMode, systemVolume)
                }
            )
        } else {
            AtchaCommonButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)
                    .align(Alignment.BottomCenter),
                buttonType = ButtonType.PRIMARY,
                buttonSize = ButtonSize.MEDIUM,
                buttonText = stringResource(R.string.volume_bottom_sheet_setting_btn_tv),
                onClick = {
                    onAlarmSelected(selectedMode, 0)
                }
            )
        }
    }
}

@Preview
@Composable
fun OnboardingAlarmSelectorPreview_1() {
    OnboardingAlarmSelector()
}

@Preview
@Composable
fun OnboardingAlarmSelector_2() {
    OnboardingAlarmSelector()
}
