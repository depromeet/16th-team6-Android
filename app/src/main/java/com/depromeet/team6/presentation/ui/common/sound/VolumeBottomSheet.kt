package com.depromeet.team6.presentation.ui.common.sound

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.type.ButtonSize
import com.depromeet.team6.presentation.type.ButtonType
import com.depromeet.team6.presentation.ui.common.button.AtchaCommonButton
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun VolumeBottomSheet(
    modifier: Modifier = Modifier,
    currentVolume: Int,
    onVolumeChanged: (Int) -> Unit,
    onButtonClicked: (Int) -> Unit
) {
    val context = LocalContext.current
    var current by remember {
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val systemMax = audio.getStreamMaxVolume(AudioManager.STREAM_ALARM)
        val volumeScale = AlarmVolumeMapper.systemToPercent(
            systemVolume = currentVolume,
            maxVolume = systemMax
        )
        mutableIntStateOf(volumeScale)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = defaultTeam6Colors.gray940
            ),
        verticalArrangement = Arrangement.Bottom
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.volume_bottom_sheet_title_tv),
                style = defaultTeam6Typography.heading3_H3SB17,
                color = LocalTeam6Colors.current.white
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.volume_bottom_sheet_setting_tv),
                style = defaultTeam6Typography.body6_B6R14,
                color = LocalTeam6Colors.current.gray200
            )

            Spacer(modifier = Modifier.height(24.dp))

            VolumeBar(
                currentVolume = current,
                onVolumeChanged = { newValue ->
                    current = newValue
                    onVolumeChanged(newValue)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            AtchaCommonButton(
                modifier = Modifier
                    .fillMaxWidth(),
                buttonType = ButtonType.PRIMARY,
                buttonSize = ButtonSize.MEDIUM,
                buttonText = stringResource(R.string.volume_bottom_sheet_setting_btn_tv),
                onClick = {
                    onButtonClicked(current)
                }
            )
        }
    }
}

@Preview
@Composable
fun VolumeBottomSheetPreview() {
    VolumeBottomSheet(
        currentVolume = 1,
        onVolumeChanged = {},
        onButtonClicked = {}
    )
}
