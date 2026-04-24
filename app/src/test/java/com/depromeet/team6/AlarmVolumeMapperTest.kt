package com.depromeet.team6

import com.depromeet.team6.presentation.ui.common.sound.AlarmVolumeMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlarmVolumeMapperTest {

    // minimumSystemVolume

    @Test
    fun `최대볼륨 10일때 최소볼륨은 1`() {
        assertEquals(1, AlarmVolumeMapper.minimumSystemVolume(10))
    }

    @Test
    fun `최대볼륨 15일때 최소볼륨은 2`() {
        assertEquals(2, AlarmVolumeMapper.minimumSystemVolume(15))
    }

    @Test
    fun `최대볼륨 0이하면 최소볼륨은 1`() {
        assertEquals(1, AlarmVolumeMapper.minimumSystemVolume(0))
        assertEquals(1, AlarmVolumeMapper.minimumSystemVolume(-5))
    }

    // systemToPercent

    @Test
    fun `시스템볼륨 0이면 최소 퍼센트(약 13퍼)로 보정`() {
        val maxVolume = 15
        val minimumVolume = AlarmVolumeMapper.minimumSystemVolume(maxVolume)
        val minimumPercent = (minimumVolume * 100f / maxVolume).toInt()
        val result = AlarmVolumeMapper.systemToPercent(systemVolume = 0, maxVolume = maxVolume)
        assertTrue(result >= minimumPercent)
    }

    @Test
    fun `시스템볼륨이 최대볼륨과 같으면 100퍼센트`() {
        assertEquals(100, AlarmVolumeMapper.systemToPercent(systemVolume = 15, maxVolume = 15))
    }

    @Test
    fun `시스템볼륨이 최대볼륨 초과해도 100퍼센트로 클램핑`() {
        assertEquals(100, AlarmVolumeMapper.systemToPercent(systemVolume = 20, maxVolume = 15))
    }

    @Test
    fun `시스템볼륨 음수면 최소 퍼센트로 보정`() {
        val maxVolume = 15
        val minimumVolume = AlarmVolumeMapper.minimumSystemVolume(maxVolume)
        val minimumPercent = (minimumVolume * 100f / maxVolume).toInt()
        val result = AlarmVolumeMapper.systemToPercent(systemVolume = -1, maxVolume = maxVolume)
        assertTrue(result >= minimumPercent)
    }

    @Test
    fun `시스템볼륨 절반이면 50퍼센트`() {
        assertEquals(50, AlarmVolumeMapper.systemToPercent(systemVolume = 5, maxVolume = 10))
    }

    // percentToSystem

    @Test
    fun `퍼센트 0이면 최소 시스템볼륨으로 보정`() {
        val result = AlarmVolumeMapper.percentToSystem(volumePercent = 0, maxVolume = 15)
        assertTrue(result >= AlarmVolumeMapper.minimumSystemVolume(15))
    }

    @Test
    fun `퍼센트 100이면 최대 시스템볼륨`() {
        assertEquals(15, AlarmVolumeMapper.percentToSystem(volumePercent = 100, maxVolume = 15))
    }

    @Test
    fun `퍼센트 100초과해도 최대볼륨으로 클램핑`() {
        assertEquals(15, AlarmVolumeMapper.percentToSystem(volumePercent = 150, maxVolume = 15))
    }

    @Test
    fun `퍼센트 음수면 최소 시스템볼륨으로 보정`() {
        val result = AlarmVolumeMapper.percentToSystem(volumePercent = -10, maxVolume = 15)
        assertTrue(result >= AlarmVolumeMapper.minimumSystemVolume(15))
    }

    @Test
    fun `퍼센트 50이면 최대볼륨의 절반`() {
        assertEquals(5, AlarmVolumeMapper.percentToSystem(volumePercent = 50, maxVolume = 10))
    }

    // 양방향 변환 일관성

    @Test
    fun `systemToPercent 후 percentToSystem하면 원래 값과 유사`() {
        val maxVolume = 15
        val original = 10
        val percent = AlarmVolumeMapper.systemToPercent(original, maxVolume)
        val restored = AlarmVolumeMapper.percentToSystem(percent, maxVolume)
        assertTrue(kotlin.math.abs(original - restored) <= 1)
    }
}
