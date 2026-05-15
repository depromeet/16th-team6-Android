package com.depromeet.team6

import com.depromeet.team6.presentation.ui.mypage.MypageViewModel
import com.google.android.play.core.install.model.UpdateAvailability
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MypageUpdateAvailabilityTest {

    // ── isUpdateAvailableFromPlayApi

    @Test
    fun `Play API UPDATE_AVAILABLE이면 업데이트 필요`() {
        assertTrue(
            MypageViewModel.isUpdateAvailableFromPlayApi(UpdateAvailability.UPDATE_AVAILABLE)
        )
    }

    @Test
    fun `Play API UPDATE_NOT_AVAILABLE이면 업데이트 불필요`() {
        assertFalse(
            MypageViewModel.isUpdateAvailableFromPlayApi(UpdateAvailability.UPDATE_NOT_AVAILABLE)
        )
    }

    @Test
    fun `Play API UNKNOWN이면 업데이트 불필요`() {
        assertFalse(
            MypageViewModel.isUpdateAvailableFromPlayApi(UpdateAvailability.UNKNOWN)
        )
    }

    @Test
    fun `Play API 업데이트 진행 중이면 업데이트 불필요`() {
        assertFalse(
            MypageViewModel.isUpdateAvailableFromPlayApi(
                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            )
        )
    }

    // ── isUpdateAvailableFromServer (폴백)

    @Test
    fun `서버 버전이 현재 버전보다 높으면 업데이트 필요`() {
        assertTrue(
            MypageViewModel.isUpdateAvailableFromServer(
                serverVersion = "v1.3.44",
                currentVersion = "1.3.43"
            )
        )
    }

    @Test
    fun `서버 버전과 현재 버전이 같으면 업데이트 불필요`() {
        assertFalse(
            MypageViewModel.isUpdateAvailableFromServer(
                serverVersion = "v1.3.43",
                currentVersion = "1.3.43"
            )
        )
    }

    @Test
    fun `서버 버전이 비어있으면 업데이트 불필요`() {
        assertFalse(
            MypageViewModel.isUpdateAvailableFromServer(
                serverVersion = "",
                currentVersion = "1.3.43"
            )
        )
    }

    @Test
    fun `서버 버전에 v 접두사 없으면 업데이트 필요로 처리`() {
        assertTrue(
            MypageViewModel.isUpdateAvailableFromServer(
                serverVersion = "1.3.44",
                currentVersion = "1.3.43"
            )
        )
    }
}
