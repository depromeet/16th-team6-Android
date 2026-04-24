package com.depromeet.team6.presentation.ui.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.repository.HomeRepository
import com.depromeet.team6.domain.repository.UserInfoRepository
import com.depromeet.team6.domain.usecase.GetRealtimeLocationUseCase
import com.depromeet.team6.presentation.util.DefaultLatLng
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.presentation.util.view.NetworkState
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val homeRepository: HomeRepository,
    private val getRealtimeLocationUseCase: GetRealtimeLocationUseCase,
    @ApplicationContext private val context: Context
) : BaseViewModel<MainContract.MainState, MainContract.MainSideEffect, MainContract.MainEvent>() {

    private var fcmToken: String? = null
    private val _currentLocation = MutableStateFlow(LatLng(DefaultLatLng.DEFAULT_LAT, DefaultLatLng.DEFAULT_LNG))
    val currentLocation: StateFlow<LatLng> = _currentLocation.asStateFlow()
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

    fun startLocationUpdates() {
        getRealtimeLocationUseCase()
            .onEach { newLocation ->
                _currentLocation.value = newLocation
            }
            .catch { e ->
                // 위치 정보를 가져오는 중 에러 발생 시 처리 (예: 로그 남기기)
                Timber.e(e, "Error while collecting location updates : $e")
            }
            .launchIn(viewModelScope) // viewModelScope에서 Flow 수집 시작
    }

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

    suspend fun checkAutoLogin(): Boolean {
        return withContext(Dispatchers.IO) {
            userInfoRepository.getRefreshToken().isNotEmpty()
        }
    }

    fun getLastCourseInfo(): CourseInfo? = homeRepository.getLastCourseInfo()

    fun getDeparturePoint(): Address? = homeRepository.getDeparturePoint()

    fun getDestinationPoint(): Address? = homeRepository.getDestinationPoint()

    fun setUserDeparture(isDeparted: Boolean) {
        homeRepository.setUserDeparture(isDeparted)
    }

    companion object {
        const val SPLASH_SCREEN_DELAY = 2000L
    }
}
