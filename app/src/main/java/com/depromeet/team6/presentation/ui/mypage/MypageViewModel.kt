package com.depromeet.team6.presentation.ui.mypage

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.BuildConfig
import com.depromeet.team6.data.dataremote.model.request.user.RequestModifyUserInfoDto
import com.depromeet.team6.data.repositoryimpl.UserInfoRepositoryImpl
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.MypageUserInfo
import com.depromeet.team6.domain.repository.HomeRepository
import com.depromeet.team6.domain.usecase.DeleteWithDrawUseCase
import com.depromeet.team6.domain.usecase.GetAddressFromCoordinatesUseCase
import com.depromeet.team6.domain.usecase.GetIsServiceRegionUseCase
import com.depromeet.team6.domain.usecase.GetLocationsUseCase
import com.depromeet.team6.domain.usecase.GetUserInfoUseCase
import com.depromeet.team6.domain.usecase.ModifyUserInfoUseCase
import com.depromeet.team6.domain.usecase.PostLogoutUseCase
import com.depromeet.team6.presentation.mapper.toPresentationList
import com.depromeet.team6.presentation.util.amplitude.AmplitudeUtils
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.context.getUserLocation
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.android.gms.maps.model.LatLng
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val userInfoRepositoryImpl: UserInfoRepositoryImpl,
    private val homeRepository: HomeRepository,
    private val postLogoutUseCase: PostLogoutUseCase,
    private val getLocationsUseCase: GetLocationsUseCase,
    private val getAddressFromCoordinatesUseCase: GetAddressFromCoordinatesUseCase,
    private val getIsServiceRegionUseCase: GetIsServiceRegionUseCase,
    private val deleteWithDrawUseCase: DeleteWithDrawUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val modifyUserInfoUseCase: ModifyUserInfoUseCase
) : BaseViewModel<MypageContract.MypageUiState, MypageContract.MypageSideEffect, MypageContract.MypageEvent>() {

    init {
        AmplitudeUtils.trackEvent(
            "마이페이지_진입"
        )
        loadAlarmSettings()
    }

    // 주소 초기화 여부를 추적하는 플래그
    private var isAddressInitialized = false

    override fun createInitialState(): MypageContract.MypageUiState = MypageContract.MypageUiState()

    override suspend fun handleEvent(event: MypageContract.MypageEvent) {
        when (event) {
            is MypageContract.MypageEvent.BackPressed -> navigateBack()
            is MypageContract.MypageEvent.LogoutClicked -> setState {
                copy(
                    logoutDialogVisible = true
                )
            }

            is MypageContract.MypageEvent.WithDrawClicked -> setState { copy(withDrawScreenVisible = true) }
            is MypageContract.MypageEvent.PolicyClicked -> setState { copy(isWebViewOpened = true) }
            is MypageContract.MypageEvent.PolicyClosed -> setState { copy(isWebViewOpened = false) }
            is MypageContract.MypageEvent.LogoutConfirmed -> logout()
            is MypageContract.MypageEvent.WithDrawConfirmed -> withDraw(event.reason)
            is MypageContract.MypageEvent.DismissDialog -> setState {
                copy(
                    logoutDialogVisible = false
                )
            }

            is MypageContract.MypageEvent.AccountClicked -> navigateToAccount()
            is MypageContract.MypageEvent.ChangeHomeClicked -> navigateToChangeHome()
            is MypageContract.MypageEvent.UpdateMyAddress -> getUserInfo()
            is MypageContract.MypageEvent.ChangeMapViewVisible -> setState {
                if (event.selectedAddress != null) {
                    copy(
                        mapViewVisible = event.mapViewVisible,
                        selectedAddress = event.selectedAddress
                    )
                } else {
                    copy(
                        mapViewVisible = event.mapViewVisible
                    )
                }
            }

            is MypageContract.MypageEvent.ClearAddress -> setState {
                copy(
                    myAddress = Address(
                        name = "",
                        lat = 0.0,
                        lon = 0.0,
                        address = ""
                    )
                )
            }

            is MypageContract.MypageEvent.ClearText -> setState {
                copy(
                    searchText = "",
                    searchLocations = emptyList()
                )
            }

            is MypageContract.MypageEvent.SearchPopUpBackPressed -> setState {
                copy(
                    searchPopupVisible = false
                )
            }

            is MypageContract.MypageEvent.ShowSearchPopup -> setState {
                copy(
                    searchPopupVisible = true
                )
            }

            is MypageContract.MypageEvent.UpdateSearchText -> handleUpdateSearchText(event = event)
            is MypageContract.MypageEvent.LocationSelectButtonClicked -> setState {
                copy(
                    searchPopupVisible = false
                )
            }

            MypageContract.MypageEvent.AlarmSettingClicked -> navigateToAlarmSetting()

            is MypageContract.MypageEvent.AlarmTypeModified -> {
                val alarmType = when (event.type) {
                    MypageContract.AlarmType.SOUND -> "소리"
                    MypageContract.AlarmType.VIBRATION -> "진동"
                    MypageContract.AlarmType.ALL -> "소리 및 진동"
                }
                AmplitudeUtils.trackEventWithProperties(
                    "알람 설정",
                    mapOf(
                        "알람_방식" to alarmType
                    )
                )
                saveAlarmType(event.type)
                navigateToMainSetting()
            }
            is MypageContract.MypageEvent.AlarmVolumeModified -> {
                saveAlarmVolume(event.volume)
                navigateToMainSetting()
            }
        }
    }

    fun getUserInfo() {
        if (isAddressInitialized && currentState.myAddress.address.isNotEmpty()) {
            Timber.d("주소가 이미 초기화되어 있어 getUserInfo에서 주소를 갱신하지 않습니다.")
            return
        }

        viewModelScope.launch {
            getUserInfoUseCase().onSuccess { userInfo ->
                Log.d("MypageVM", "appVersion from server: ${userInfo.appVersion}")
                setLocationToHomeAddress(userInfo.userHome.latitude, userInfo.userHome.longitude)
                setState {
                    copy(
                        myAddress = Address(
                            name = userInfo.address,
                            lat = userInfo.userHome.latitude,
                            lon = userInfo.userHome.longitude,
                            address = currentState.myAddress.address
                        )
                    )
                }

                setState {
                    copy(
                        userInfo = MypageUserInfo(
                            address = userInfo.address,
                            lat = userInfo.userHome.latitude,
                            lon = userInfo.userHome.longitude,
                            fcmToken = null,
                            appVersion = userInfo.appVersion
                        )
                    )
                }
                isAddressInitialized = true
            }
                .onFailure { exception ->
                    handleApiException(exception)
                }
        }
    }

    private var debounceJob: Job? = null

    private fun handleUpdateSearchText(event: MypageContract.MypageEvent.UpdateSearchText) {
        setState { copy(searchText = event.text) }
        debounceJob?.cancel()
        if (event.text.isNotEmpty()) {
            debounceJob = viewModelScope.launch {
                delay(300)
                getLocationsUseCase(
                    keyword = event.text,
                    lat = currentState.userCurrentLocation.latitude,
                    lon = currentState.userCurrentLocation.longitude
                ).onSuccess { locations ->
                    setState {
                        copy(
                            searchLocations = locations.toPresentationList()
                        )
                    }
                }.onFailure { exception ->
                    setState { copy(searchLocations = emptyList()) }
                    handleApiException(exception = exception)
                }
            }
        }
    }

    fun modifyUserAddress(callback: () -> Unit = {}) {
        setState {
            copy(
                myAddress = currentState.selectedAddress
            )
        }
        viewModelScope.launch {
            val currentAddress = currentState.myAddress

            val modifyUserInfoDto = RequestModifyUserInfoDto(
                address = currentAddress.name,
                lat = currentAddress.lat,
                lon = currentAddress.lon
            )

            modifyUserInfoUseCase(modifyUserInfoDto = modifyUserInfoDto)
                .onSuccess { userInfo ->
                    setState {
                        copy(
                            myAddress = Address(
                                name = currentAddress.name,
                                lat = userInfo.userHome.latitude,
                                lon = userInfo.userHome.longitude,
                                address = currentAddress.address
                            )
                        )
                    }

                    setState {
                        copy(
                            userInfo = currentState.userInfo.copy(
                                address = currentAddress.name,
                                lat = userInfo.userHome.latitude,
                                lon = userInfo.userHome.longitude
                            )
                        )
                    }

                    setState { copy(mapViewVisible = false) }

                    callback()
                }
                .onFailure { exception ->
                    handleApiException(exception)
                }
        }
    }

    fun validateAndModifyUserAddress(callback: () -> Unit = {}) {
        val selectedAddress = currentState.selectedAddress
        viewModelScope.launch {
            getIsServiceRegionUseCase(
                lat = selectedAddress.lat,
                lon = selectedAddress.lon
            ).onSuccess { isServiceRegion ->
                if (isServiceRegion) {
                    modifyUserAddress(callback = callback)
                } else {
                    setSideEffect(MypageContract.MypageSideEffect.ShowOutOfServiceRegionBottomSheet)
                }
            }.onFailure {
                setSideEffect(MypageContract.MypageSideEffect.ShowOutOfServiceRegionBottomSheet)
            }
        }
    }

    fun navigateToPlayStore(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=${context.packageName}")
                setPackage("com.android.vending")
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
            }
            context.startActivity(intent)
        }
    }

    fun checkUpdateAvailability(
        context: Context,
        appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)
    ) {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                val isUpdateAvailable = isUpdateAvailableFromPlayApi(appUpdateInfo.updateAvailability())
                setState { copy(isUpdateAvailable = isUpdateAvailable) }
            }
            .addOnFailureListener {
                fallbackToServerVersion()
            }
    }

    private fun fallbackToServerVersion() {
        val isUpdateAvailable = isUpdateAvailableFromServer(
            serverVersion = currentState.userInfo.appVersion.orEmpty(),
            currentVersion = BuildConfig.VERSION_NAME
        )
        setState { copy(isUpdateAvailable = isUpdateAvailable) }
    }

    private fun setLocationToHomeAddress(
        lat: Double,
        lon: Double
    ) {
        viewModelScope.launch {
            getAddressFromCoordinatesUseCase(lat, lon)
                .onSuccess { address ->
                    setState {
                        copy(
                            myAddress = myAddress.copy(
                                address = address.address
                            )
                        )
                    }
                }
                .onFailure { exception ->
                    handleApiException(exception)
                }
//            getAddressFromCoordinatesUseCase.invoke(lat, lon)
//                .onSuccess { address ->
//                    setState {
//                        copy(
//                            myAdress = myAdress.copy(
//                                address = address.address
//                            )
//                        )
//                    }
//                }.onFailure {
//                    Timber.e("주소 변환 실패: ${it.message}")
//                }
        }
    }

    fun getCenterLocation(location: LatLng, onComplete: (Address) -> Unit = {}) {
        viewModelScope.launch {
            getAddressFromCoordinatesUseCase(location.latitude, location.longitude)
                .onSuccess { address ->
                    setState { copy(selectedAddress = address) }
                    onComplete(address)
                }
                .onFailure { exception ->
                    handleApiException(exception)
                }

//            getAddressFromCoordinatesUseCase(location.latitude, location.longitude)
//                .onSuccess { address ->
//                    setState { copy(myAdress = address) }
//                    onComplete(address)
//                }
//                .onFailure {
//                    Timber.e("주소 변환 실패: ${it.message}")
//                }
        }
    }

    fun updateUserLocation(context: Context) {
        viewModelScope.launch {
            if (PermissionUtil.hasLocationPermissions(context)) {
                val location = context.getUserLocation()
                setState {
                    copy(
                        userCurrentLocation = location
                    )
                }
            }
        }
    }

    private fun saveAlarmType(type: MypageContract.AlarmType) {
        viewModelScope.launch {
            try {
                val isSound = when (type) {
                    MypageContract.AlarmType.SOUND -> true
                    MypageContract.AlarmType.VIBRATION -> false
                    MypageContract.AlarmType.ALL -> true
                }
                val isVibrate = when (type) {
                    MypageContract.AlarmType.SOUND -> false
                    MypageContract.AlarmType.VIBRATION -> true
                    MypageContract.AlarmType.ALL -> true
                }
                userInfoRepositoryImpl.saveIsAlarmSound(isSound)
                userInfoRepositoryImpl.saveIsAlarmVibrate(isVibrate)
                setState {
                    copy(selectedAlarmType = type)
                }
            } catch (e: Exception) {
                Timber.e("알람 설정 저장 실패: ${e.message}")
            }
        }
    }

    private fun saveAlarmVolume(volume: Int) {
        viewModelScope.launch {
            userInfoRepositoryImpl.saveAlarmVolume(volume)
            setState {
                copy(
                    alarmVolume = volume
                )
            }
        }
    }

    private fun loadAlarmSettings() {
        viewModelScope.launch {
            try {
                val isSound = userInfoRepositoryImpl.getIsAlarmSound()
                val isVibrate = userInfoRepositoryImpl.getIsAlarmVibrate()
                val alarmVolume = userInfoRepositoryImpl.getAlarmVolume()

                val alarmType = when {
                    isSound && isVibrate -> MypageContract.AlarmType.ALL
                    isSound -> MypageContract.AlarmType.SOUND
                    isVibrate -> MypageContract.AlarmType.VIBRATION
                    else -> MypageContract.AlarmType.ALL
                }

                setState {
                    copy(
                        selectedAlarmType = alarmType,
                        alarmVolume = alarmVolume
                    )
                }
            } catch (e: Exception) {
                Timber.e("알람 설정 불러오기 실패: ${e.message}")
                setState {
                    copy(selectedAlarmType = MypageContract.AlarmType.SOUND)
                }
            }
        }
    }

    private fun logout() {
        userInfoRepositoryImpl.setAccessToken(userInfoRepositoryImpl.getRefreshToken())
        viewModelScope.launch {
            postLogoutUseCase().onSuccess {
                setSideEffect(MypageContract.MypageSideEffect.NavigateToLogin)
                setState { copy(loadState = LoadState.Error) }
                userInfoRepositoryImpl.clear()
                homeRepository.clearAlarmData()
                AmplitudeUtils.trackEvent(
                    "로그아웃"
                )
            }.onFailure { exception ->
                setEvent(MypageContract.MypageEvent.LogoutClicked)
                handleApiException(exception = exception)
            }
        }
    }

    private fun withDraw(reason: String) {
        viewModelScope.launch {
            deleteWithDrawUseCase(reason).onSuccess {
                AmplitudeUtils.trackEvent(
                    "회원탈퇴"
                )
                userInfoRepositoryImpl.clear()
                setSideEffect(MypageContract.MypageSideEffect.ClearPermissionData)
                homeRepository.clearAlarmData()
                setSideEffect(MypageContract.MypageSideEffect.NavigateToLogin)
            }.onFailure { exception ->
                handleApiException(exception = exception)
            }
        }
    }

    private fun navigateToAccount() {
        setState {
            copy(
                currentScreen = MypageContract.MypageScreen.ACCOUNT,
                withDrawScreenVisible = false
            )
        }
    }

    private fun navigateToChangeHome() {
        setState { copy(currentScreen = MypageContract.MypageScreen.CHANGE_HOME) }
    }

    private fun navigateToAlarmSetting() {
        setState { copy(currentScreen = MypageContract.MypageScreen.ALARM) }
    }

    private fun navigateToMainSetting() {
        setState { copy(currentScreen = MypageContract.MypageScreen.MAIN) }
    }

    private fun navigateBack() {
        val currentScreen = currentState.currentScreen
        if (currentScreen == MypageContract.MypageScreen.MAIN) {
            setSideEffect(MypageContract.MypageSideEffect.NavigateBack)
        } else {
            navigateToMainSetting()
        }
//        when (currentScreen) {
//            MypageContract.MypageScreen.MAIN -> {
//                setSideEffect(MypageContract.MypageSideEffect.NavigateBack)
//            }
//
//            MypageContract.MypageScreen.ACCOUNT -> {
//                navigateToMainSetting()
//            }
//
//            MypageContract.MypageScreen.CHANGE_HOME -> {
//                navigateToMainSetting()
//            }
//
//            MypageContract.MypageScreen.ALARM -> {
//                navigateToMainSetting()
//            }
//        }
    }

    companion object {
        internal fun isUpdateAvailableFromPlayApi(updateAvailability: Int): Boolean =
            updateAvailability == UpdateAvailability.UPDATE_AVAILABLE

        internal fun isUpdateAvailableFromServer(serverVersion: String, currentVersion: String): Boolean =
            serverVersion.isNotEmpty() && serverVersion != "v$currentVersion"
    }
}
