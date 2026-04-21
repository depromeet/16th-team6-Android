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
        val clampedVolume = systemVolume.coerceIn(0, safeMaxVolume)
        val minimumPercent = minimumSystemVolume(safeMaxVolume)

        return (clampedVolume * 100f / safeMaxVolume).roundToInt()
            .coerceIn(minimumPercent, 100)
    }

    fun percentToSystem(volumePercent: Int, maxVolume: Int): Int {
        val safeMaxVolume = maxVolume.coerceAtLeast(1)
        val clampedPercent = volumePercent.coerceIn(0, 100)
        val minimumVolume = minimumSystemVolume(safeMaxVolume)

        return (clampedPercent / 100f * safeMaxVolume).roundToInt()
            .coerceIn(minimumVolume, safeMaxVolume)
    }

    private const val MIN_VOLUME_RATIO = 0.10f
}
