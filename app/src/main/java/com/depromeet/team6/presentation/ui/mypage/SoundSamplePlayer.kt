package com.depromeet.team6.presentation.ui.mypage

import android.content.Context
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class SoundSamplePlayer(
    context: Context
) {
    // 기본 알림음 (소리 선택 시)
    private val notificationRingtone: Ringtone =
        RingtoneManager.getRingtone(
            context,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        )

    // 볼륨 샘플용 시스템 톤 (슬라이더 선택 시)
    private var toneGenerator = ToneGenerator(
        AudioManager.STREAM_NOTIFICATION,
        100 // 실제 볼륨은 playWithVolume에서 조절
    )

    // 라디오 버튼에서 소리 선택 시 피드백
    fun playNotificationSample() {
        if (notificationRingtone.isPlaying) {
            notificationRingtone.stop()
        }
        notificationRingtone.play()
    }

    // 슬라이더 볼륨 조절 시 소리 피드백
    fun playVolumeSample(volume: Float) {
        val scaled = (kotlin.math.sqrt(volume) * 100)
            .coerceAtLeast(10f)
            .toInt()

        // 이전 톤 정리
        toneGenerator.release()

        toneGenerator = ToneGenerator(
            AudioManager.STREAM_NOTIFICATION,
            scaled
        ).apply {
            startTone(ToneGenerator.TONE_PROP_BEEP, 120)
        }
    }

    fun release() {
        toneGenerator.release()
        if (notificationRingtone.isPlaying) {
            notificationRingtone.stop()
        }
    }

    // 진동 피드백
    fun vibrateSample(context: Context) {
        val vibrator = getVibrator(context)
        if (!vibrator.hasVibrator()) return

        val effect = VibrationEffect.createOneShot(
            150,
            VibrationEffect.DEFAULT_AMPLITUDE
        )

        vibrator.vibrate(effect)
    }

    private fun getVibrator(context: Context): Vibrator {
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
}
