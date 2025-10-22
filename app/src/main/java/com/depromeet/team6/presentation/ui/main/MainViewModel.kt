package com.depromeet.team6.presentation.ui.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.repository.UserInfoRepository
import com.depromeet.team6.domain.usecase.GetAppVersionUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.presentation.util.view.NetworkState
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val getAppVersionUseCase: GetAppVersionUseCase,
    @ApplicationContext private val context: Context
) : BaseViewModel<MainContract.MainState, MainContract.MainSideEffect, MainContract.MainEvent>() {

    private var fcmToken: String? = null

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // UI에 노출할 네트워크 상태 StateFlow
    val networkAvailability: StateFlow<NetworkState> = observeNetworkStatus()
        .stateIn(
            // viewModelScope를 사용하여 ViewModel 생명주기 동안 Flow가 활성화되도록 합니다.
            scope = viewModelScope,
            // 앱이 백그라운드에 가도 5초 동안은 연결을 유지하여 불필요한 재연결을 방지합니다.
            started = SharingStarted.WhileSubscribed(5_000),
            // Flow의 초기값을 true로 설정합니다.
            initialValue = NetworkState.Available
        )

    init {
        loadInitialData()
        fetchFcmToken()
        checkAppVersion()
    }

    override fun createInitialState(): MainContract.MainState = MainContract.MainState()

    override suspend fun handleEvent(event: MainContract.MainEvent) {
    }

    // Splash 화면에서 데이터 로드
    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                val checkAutoLoginDeferred = async { checkAutoLogin() }
                // 🔹 SplashScreen 2초 후 종료
                val timerDeferred = launch { delay(SPLASH_SCREEN_DELAY) }

                val isAutoLogin = checkAutoLoginDeferred.await()
                timerDeferred.join()

                setState {
                    copy(
                        splashState = LoadState.Success,
                        autoLogin = isAutoLogin
                    )
                }
            } catch (e: Exception) {
                setState {
                    copy(
                        splashState = LoadState.Success,
                        autoLogin = false
                    )
                }
            }
        }
    }

    // 네트워크 상태를 Flow로 제공
    private fun observeNetworkStatus(): Flow<NetworkState> = callbackFlow {
        // 1. 네트워크 상태 변경을 수신할 콜백 객체 생성
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            // 네트워크가 사용 가능해질 때 호출됨
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(NetworkState.Available) // 'Available' 상태 전송
            }

            // 네트워크 연결이 끊어졌을 때 호출됨
            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(NetworkState.Unavailable) // 'Unavailable' 상태 전송
            }
        }

        // 2. 현재 네트워크 상태를 즉시 확인하여 첫 상태를 전송
        val currentNetwork = connectivityManager.activeNetwork
        if (currentNetwork == null) {
            trySend(NetworkState.Unavailable)
        } else {
            val capabilities = connectivityManager.getNetworkCapabilities(currentNetwork)
            if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                trySend(NetworkState.Available)
            } else {
                trySend(NetworkState.Unavailable)
            }
        }

        // 3. 콜백 등록
        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        // 4. Flow가 수집을 중단하면(취소되면) 콜백을 해제하여 메모리 누수 방지
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }.distinctUntilChanged() // 연속으로 중복된 상태가 전송되는 것을 방지

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

    private fun checkAppVersion() {
        val installedVersion = getCurrentVersionName()?.cleanVersion() ?: "0.0.0"
        viewModelScope.launch {
            getAppVersionUseCase().onSuccess { appVersion ->
                val latestVersion = appVersion.removePrefix("Success(")
                    .removePrefix("v")
                    .removeSuffix(")")
                    .cleanVersion()

                Timber.d("latestVersion: $latestVersion, installedVersion: $installedVersion")

                when (compareVersions(installedVersion, latestVersion)) {
                    VersionResult.UPDATE_REQUIRED -> {
                        setSideEffect(MainContract.MainSideEffect.ShowUpdateRequiredDialog)
                    }

                    VersionResult.UPDATE_OPTIONAL -> {
                        setSideEffect(MainContract.MainSideEffect.ShowUpdateOptionalDialog)
                    }

                    VersionResult.UP_TO_DATE -> {
                        Timber.d("최신 버전입니다")
                    }
                }
            }.onFailure {
                Timber.e(it, "앱 버전 확인 실패")
            }
        }
    }

    private fun String.cleanVersion(): String =
        this.replace("[^0-9.]".toRegex(), "")

    private fun compareVersions(installed: String, latest: String): VersionResult {
        if (com.depromeet.team6.BuildConfig.DEBUG) return VersionResult.UP_TO_DATE

        val installedParts = installed.split(".").map { it.toIntOrNull() ?: 0 }
        val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }

        val (a1, b1, c1) = installedParts + List(3 - installedParts.size) { 0 }
        val (a2, b2, c2) = latestParts + List(3 - latestParts.size) { 0 }

        return when {
            a1 < a2 || b1 < b2 -> VersionResult.UPDATE_REQUIRED
            c1 < c2 -> VersionResult.UPDATE_OPTIONAL
            else -> VersionResult.UP_TO_DATE
        }
    }

    private enum class VersionResult {
        UPDATE_REQUIRED,
        UPDATE_OPTIONAL,
        UP_TO_DATE
    }

    private fun getCurrentVersionName(): String? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            Timber.e(e, "Failed to get app version name")
            "0.0.0"
        }
    }

    suspend fun checkAutoLogin(): Boolean {
        return withContext(Dispatchers.IO) {
            userInfoRepository.getRefreshToken().isNotEmpty()
        }
    }

    companion object {
        const val SPLASH_SCREEN_DELAY = 2000L
    }
}
