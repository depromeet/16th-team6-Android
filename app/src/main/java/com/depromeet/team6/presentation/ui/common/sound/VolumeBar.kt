package com.depromeet.team6.presentation.ui.common.sound

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import kotlin.math.ceil
import kotlin.math.roundToInt

private const val VOLUME_MIN = 10
private const val VOLUME_MAX = 100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolumeBar(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val audio = remember(context) {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    val systemMax =
        remember { audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1) }

    var current by remember { mutableIntStateOf(50) }

    val minSystem = remember(systemMax) {
        ceil(systemMax * 0.10f).toInt().coerceAtLeast(1)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            Slider(
                value = current.toFloat(),
                onValueChange = { v ->
                    val vol = v.toInt().coerceIn(VOLUME_MIN, VOLUME_MAX)
                    current = vol

                    val mapped = (vol / 100f * systemMax).roundToInt()
                    val systemVolume = mapped.coerceAtLeast(minSystem) // 최소 10%

                    audio.setStreamVolume(AudioManager.STREAM_MUSIC, systemVolume, 0)
                },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = LocalTeam6Colors.current.white,
                    activeTrackColor = LocalTeam6Colors.current.systemGreen,
                    inactiveTrackColor = LocalTeam6Colors.current.gray200,
                )
            )
        }
    }
}

@Preview
@Composable
fun VolumeBarPreview() {
    VolumeBar()
}