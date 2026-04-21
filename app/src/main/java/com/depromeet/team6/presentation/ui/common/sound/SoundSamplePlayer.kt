package com.depromeet.team6.presentation.ui.common.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlin.math.roundToInt

class SoundSamplePlayer(
    private val context: Context
) {
    private val alarmRingtone: Ringtone? =
        RingtoneManager.getRingtone(
            context,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        )?.apply {
            audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
        }

    private var toneGenerator: ToneGenerator? = null
    private var currentToneVolume = DEFAULT_TONE_VOLUME
    private var lastPlayedToneVolume = -1
    private var lastTonePlayedAt = 0L

    fun playAlarmSample() {
        alarmRingtone?.let { ringtone ->
            if (ringtone.isPlaying) {
                ringtone.stop()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val systemMax = audio.getStreamMaxVolume(AudioManager.STREAM_ALARM).coerceAtLeast(1)
                val currentRatio = audio.getStreamVolume(AudioManager.STREAM_ALARM) / systemMax.toFloat()
                ringtone.volume = currentRatio.coerceAtLeast(MIN_SLIDER_VOLUME_RATIO)
            }
            ringtone.play()
        }
    }

    fun playCombinedSample() {
        vibrateSample()
        playAlarmSample()
    }

    fun playVolumeSample(volume: Float) {
        stopAlarmSample()

        val scaledVolume = calculateSampleToneVolume(volume)
        val now = SystemClock.elapsedRealtime()

        if (
            scaledVolume == lastPlayedToneVolume &&
            now - lastTonePlayedAt < TONE_DEBOUNCE_MILLIS
        ) {
            return
        }

        if (scaledVolume != currentToneVolume) {
            recreateToneGenerator(scaledVolume)
        }

        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, TONE_DURATION_MILLIS)
        lastPlayedToneVolume = scaledVolume
        lastTonePlayedAt = now
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null

        stopAlarmSample()
    }

    fun vibrateSample() {
        stopAlarmSample()
        val vibrator = getVibrator()
        if (!vibrator.hasVibrator()) return

        val effect = VibrationEffect.createOneShot(
            VIBRATION_DURATION_MILLIS,
            VibrationEffect.DEFAULT_AMPLITUDE
        )

        vibrator.vibrate(effect)
    }

    private fun recreateToneGenerator(volume: Int) {
        toneGenerator?.release()
        toneGenerator = runCatching {
            ToneGenerator(AudioManager.STREAM_ALARM, volume)
        }.getOrNull()
        currentToneVolume = volume
    }

    private fun calculateSampleToneVolume(volume: Float): Int {
        val normalizedVolume = volume.coerceIn(MIN_SLIDER_VOLUME_RATIO, 1f)
        val adjustedRange = (normalizedVolume - MIN_SLIDER_VOLUME_RATIO) /
            (1f - MIN_SLIDER_VOLUME_RATIO)

        return (MIN_AUDIBLE_SAMPLE_VOLUME + adjustedRange * (100 - MIN_AUDIBLE_SAMPLE_VOLUME))
            .roundToInt()
    }

    private fun stopAlarmSample() {
        alarmRingtone?.takeIf { it.isPlaying }?.stop()
    }

    private fun getVibrator(): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(
                Context.VIBRATOR_MANAGER_SERVICE
            ) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    companion object {
        private const val DEFAULT_TONE_VOLUME = 100
        private const val MIN_AUDIBLE_SAMPLE_VOLUME = 40
        private const val MIN_SLIDER_VOLUME_RATIO = 0.10f
        private const val TONE_DURATION_MILLIS = 120
        private const val TONE_DEBOUNCE_MILLIS = 80L
        private const val VIBRATION_DURATION_MILLIS = 150L
    }
}
