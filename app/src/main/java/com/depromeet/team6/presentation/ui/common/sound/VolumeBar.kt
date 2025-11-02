package com.depromeet.team6.presentation.ui.common.sound

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

private const val VOLUME_MIN = 10
private const val VOLUME_MAX = 100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolumeBar(
    modifier: Modifier = Modifier,
    currentVolume: Int = 50,
    onVolumeChanged: (Int) -> Unit
) {
    val context = LocalContext.current
    var current by remember { mutableIntStateOf(currentVolume) }

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
                    onVolumeChanged(current)
                },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = LocalTeam6Colors.current.white,
                    activeTrackColor = LocalTeam6Colors.current.systemGreen,
                    inactiveTrackColor = LocalTeam6Colors.current.gray200
                )
            )
        }
    }
}

@Preview
@Composable
fun VolumeBarPreview() {
    VolumeBar(
        currentVolume = 50,
        onVolumeChanged = {}
    )
}
