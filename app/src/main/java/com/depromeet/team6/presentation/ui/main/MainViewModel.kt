package com.depromeet.team6.presentation.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.data.datalocal.manager.LockServiceManager
import com.depromeet.team6.domain.repository.UserInfoRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val lockServiceManager: LockServiceManager
) : ViewModel() {

    private var fcmToken: String? = null

    private val _showSplash = MutableLiveData(true)
    val showSplash: LiveData<Boolean> = _showSplash

    /**
     * 🔹 FCM 토큰 가져오기 & 저장
     */
    fun fetchFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                fcmToken = token
                Timber.d("FCM Token: $token")
                userInfoRepository.setFcmToken(token)
            } else {
                Timber.e("Fetching FCM token failed")
            }
        }
    }

    /**
     * 🔹 SplashScreen 2초 후 종료
     */
    fun startSplashTimer() {
        viewModelScope.launch {
            delay(SPLASH_SCREEN_DELAY)
            _showSplash.value = false
        }
    }

    /**
     * 🔹 Lock Service 실행
     */
    fun startLockService() {
        lockServiceManager.start()
    }

    companion object {
        const val SPLASH_SCREEN_DELAY = 2000L
    }
}
