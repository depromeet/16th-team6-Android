package com.depromeet.team6.presentation.ui.common.sound

import kotlin.math.ceil
import kotlin.math.roundToInt

object AlarmVolumeMapper {
    fun minimumSystemVolume(maxVolume: Int): Int {
        return ceil(maxVolume.coerceAtLeast(1) * MIN_VOLUME_RATIO).toInt()
            .coerceAtLeast(1)
    }

    fun systemToPercent(systemVolume: Int, maxVolume: Int): Int {
        val safeMaxVolume = maxVolume.coerceAtLeast(1)
        val minimumPercent = minimumSystemVolume(safeMaxVolume)

        return (systemVolume * 100f / safeMaxVolume).roundToInt()
            .coerceAtLeast(minimumPercent)
    }

    fun percentToSystem(volumePercent: Int, maxVolume: Int): Int {
        val safeMaxVolume = maxVolume.coerceAtLeast(1)
        val minimumVolume = minimumSystemVolume(safeMaxVolume)

        return (volumePercent / 100f * safeMaxVolume).roundToInt()
            .coerceAtLeast(minimumVolume)
    }

    private const val MIN_VOLUME_RATIO = 0.10f
}
