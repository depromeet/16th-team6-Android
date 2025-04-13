package com.depromeet.team6.presentation.ui.mypage

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.R
import com.depromeet.team6.data.dataremote.model.request.user.RequestModifyUserInfoDto
import com.depromeet.team6.data.repositoryimpl.UserInfoRepositoryImpl
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.MypageUserInfo
import com.depromeet.team6.domain.usecase.DeleteWithDrawUseCase
import com.depromeet.team6.domain.usecase.GetAddressFromCoordinatesUseCase
import com.depromeet.team6.domain.usecase.GetLocationsUseCase
import com.depromeet.team6.domain.usecase.GetUserInfoUseCase
import com.depromeet.team6.domain.usecase.ModifyUserInfoUseCase
import com.depromeet.team6.domain.usecase.PostLogoutUseCase
import com.depromeet.team6.presentation.mapper.toPresentationList
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.context.getUserLocation
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.toast.atChaToastMessage
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val userInfoRepositoryImpl: UserInfoRepositoryImpl,
    private val postLogoutUseCase: PostLogoutUseCase,
    private val getLocationsUseCase: GetLocationsUseCase,
    private val getAddressFromCoordinatesUseCase: GetAddressFromCoordinatesUseCase,
    private val deleteWithDrawUseCase: DeleteWithDrawUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val modifyUserInfoUseCase: ModifyUserInfoUseCase
) : BaseViewModel<MypageContract.MypageUiState, MypageContract.MypageSideEffect, MypageContract.MypageEvent>() {

    // 주소 초기화 여부를 추적하는 플래그
    private var isAddressInitialized = false

    override fun createInitialState(): MypageContract.MypageUiState = MypageContract.MypageUiState()

    override suspend fun handleEvent(event: MypageContract.MypageEvent) {
        when (event) {
            is MypageContract.MypageEvent.BackPressed -> navigateBack()
            is MypageContract.MypageEvent.LogoutClicked -> setState { copy(logoutDialogVisible = true, withDrawDialogVisible = false) }
            is MypageContract.MypageEvent.WithDrawClicked -> setState { copy(withDrawDialogVisible = true) }
            is MypageContract.MypageEvent.PolicyClicked -> setState { copy(isWebViewOpened = true) }
            is MypageContract.MypageEvent.PolicyClosed -> setState { copy(isWebViewOpened = false) }
            is MypageContract.MypageEvent.LogoutConfirmed -> logout()
            is MypageContract.MypageEvent.WithDrawConfirmed -> withDraw()
            is MypageContract.MypageEvent.DismissDialog -> setState { copy(logoutDialogVisible = false, withDrawDialogVisible = false) }
            is MypageContract.MypageEvent.AccountClicked -> navigateToAccount()
            is MypageContract.MypageEvent.ChangeHomeClicked -> navigateToChangeHome()
            is MypageContract.MypageEvent.UpdateMyAddress -> getUserInfo()
            is MypageContract.MypageEvent.ChangeMapViewVisible -> setState {
                copy(mapViewVisible = event.mapViewVisible)
            }
            is MypageContract.MypageEvent.ClearAddress -> setState {
                copy(
                    myAdress = Address(
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
                    myAdress = event.mypageSearchLocation,
                    searchPopupVisible = false
                )
            }

            MypageContract.MypageEvent.AlarmSettingClicked -> navigateToAlarmSetting()
            is MypageContract.MypageEvent.AlarmTypeSelected -> {
                setState { copy(selectedAlarmType = event.type) }
                saveAlarmSettings(event.type)
            }
            MypageContract.MypageEvent.SoundSettingClicked -> {
                setState {
                    copy(alarmScreenState = MypageContract.AlarmScreenState.SOUND_SETTING)
                }
                loadAlarmSettings()
            }

            MypageContract.MypageEvent.TimeSettingClicked -> {
                setState {
                    copy(alarmScreenState = MypageContract.AlarmScreenState.TIME_SETTING)
                }
            }

            is MypageContract.MypageEvent.UpdateAlertFrequencies -> setState {
                copy(alertFrequencies = event.alertFrequencies)
            }
        }
    }

    fun getUserInfo() {
        if (isAddressInitialized && currentState.myAdress.address.isNotEmpty()) {
            Timber.d("주소가 이미 초기화되어 있어 getUserInfo에서 주소를 갱신하지 않습니다.")
            return
        }

        viewModelScope.launch {
            getUserInfoUseCase().onSuccess { userInfo ->
                setLocationToHomeAddress(userInfo.userHome.latitude, userInfo.userHome.longitude)
                setState {
                    copy(
                        myAdress = Address(
                            name = userInfo.address,
                            lat = userInfo.userHome.latitude,
                            lon = userInfo.userHome.longitude,
                            address = currentState.myAdress.address
                        )
                    )
                }

                setState {
                    copy(
                        userInfo = MypageUserInfo(
                            nickname = userInfo.nickname,
                            profileImageUrl = userInfo.profileImageUrl,
                            address = userInfo.address,
                            lat = userInfo.userHome.latitude,
                            lon = userInfo.userHome.longitude,
                            alertFrequencies = userInfo.alertFrequencies,
                            fcmToken = null
                        )
                    )
                }

                setState {
                    copy(
                        alertFrequencies = userInfo.alertFrequencies
                    )
                }
                isAddressInitialized = true
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
                }.onFailure {
                    setState { copy(searchLocations = emptyList()) }
                }
            }
        }
    }

    fun modifyUserAddress(context: Context) {
        viewModelScope.launch {
            try {
                val currentAddress = currentState.myAdress

                val modifyUserInfoDto = RequestModifyUserInfoDto(
                    address = currentAddress.name,
                    lat = currentAddress.lat,
                    lon = currentAddress.lon
                )

                modifyUserInfoUseCase(modifyUserInfoDto = modifyUserInfoDto)
                    .onSuccess { userInfo ->
                        setState {
                            copy(
                                myAdress = Address(
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

                        atChaToastMessage(context, R.string.mypage_change_home_toast_text, Toast.LENGTH_SHORT)
                    }
                    .onFailure { error ->
                        Timber.e("주소 업데이트 실패: ${error.message}")
                        setState { copy(loadState = LoadState.Error) }
                    }
            } catch (e: Exception) {
                Timber.e("주소 업데이트 중 예외 발생: ${e.message}")
                e.printStackTrace()
                setState { copy(loadState = LoadState.Error) }
            }
        }
    }

    fun modifyAlarmFrequencies(context: Context) {
        viewModelScope.launch {
            try {
                val modifyUserInfoDto = RequestModifyUserInfoDto(
                    alertFrequencies = currentState.alertFrequencies
                )

                modifyUserInfoUseCase(modifyUserInfoDto = modifyUserInfoDto)
                    .onSuccess { userInfo ->
                        setState {
                            copy(
                                userInfo = currentState.userInfo.copy(
                                    alertFrequencies = userInfo.alertFrequencies
                                )
                            )
                        }
                        atChaToastMessage(context, R.string.mypage_change_alarm_time_toast_text, Toast.LENGTH_SHORT)
                    }
                    .onFailure { error ->
                        Timber.e("알림 설정 변경 실패: ${error.message}")
                        setState { copy(loadState = LoadState.Error) }
                    }
            } catch (e: Exception) {
                Timber.e("알림 설정 중 예외 발생: ${e.message}")
                e.printStackTrace()
                setState { copy(loadState = LoadState.Error) }
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
        } catch (e: android.content.ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
            }
            context.startActivity(intent)
        }
    }

    private fun setLocationToHomeAddress(
        lat: Double,
        lon: Double
    ) {
        viewModelScope.launch {
            getAddressFromCoordinatesUseCase.invoke(lat, lon)
                .onSuccess { address ->
                    setState {
                        copy(
                            myAdress = myAdress.copy(
                                address = address.address
                            )
                        )
                    }
                }.onFailure {
                    Timber.e("주소 변환 실패: ${it.message}")
                }
        }
    }

    fun getCenterLocation(location: LatLng, onComplete: (Address) -> Unit = {}) {
        viewModelScope.launch {
            getAddressFromCoordinatesUseCase(location.latitude, location.longitude)
                .onSuccess { address ->
                    setState { copy(myAdress = address) }
                    onComplete(address)
                }
                .onFailure {
                    Timber.e("주소 변환 실패: ${it.message}")
                }
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

    private fun saveAlarmSettings(type: MypageContract.AlarmType) {
        viewModelScope.launch {
            try {
                val isSound = when (type) {
                    MypageContract.AlarmType.SOUND -> true
                    MypageContract.AlarmType.VIBRATION -> false
                }
                userInfoRepositoryImpl.saveAlarmSound(isSound)
            } catch (e: Exception) {
                Timber.e("알람 설정 저장 실패: ${e.message}")
            }
        }
    }

    private fun loadAlarmSettings() {
        viewModelScope.launch {
            try {
                val isSound = userInfoRepositoryImpl.getAlarmSound()

                val alarmType = if (isSound) {
                    MypageContract.AlarmType.SOUND
                } else {
                    MypageContract.AlarmType.VIBRATION
                }

                setState {
                    copy(selectedAlarmType = alarmType)
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
            if (postLogoutUseCase().isSuccessful) {
                setSideEffect(MypageContract.MypageSideEffect.NavigateToLogin)
                setState { copy(loadState = LoadState.Error) }
                userInfoRepositoryImpl.clear()
            } else {
                setEvent(MypageContract.MypageEvent.LogoutClicked)
            }
        }
    }

    private fun withDraw() {
        viewModelScope.launch {
            if (deleteWithDrawUseCase().isSuccessful) {
                userInfoRepositoryImpl.clear()
                setSideEffect(MypageContract.MypageSideEffect.NavigateToLogin)
            } else {
                setEvent(MypageContract.MypageEvent.WithDrawClicked)
            }
        }
    }

    private fun navigateToAccount() {
        setState { copy(currentScreen = MypageContract.MypageScreen.ACCOUNT) }
    }

    private fun navigateToChangeHome() {
        setState { copy(currentScreen = MypageContract.MypageScreen.CHANGE_HOME) }
    }

    private fun navigateToAlarmSetting() {
        setState { copy(currentScreen = MypageContract.MypageScreen.ALARM) }
    }

    private fun navigateBack() {
        val currentScreen = currentState.currentScreen
        if (currentScreen == MypageContract.MypageScreen.MAIN) {
            setSideEffect(MypageContract.MypageSideEffect.NavigateBack)
        } else if (currentScreen == MypageContract.MypageScreen.ACCOUNT) {
            setState { copy(currentScreen = MypageContract.MypageScreen.MAIN) }
        } else if (currentScreen == MypageContract.MypageScreen.CHANGE_HOME) {
            setState { copy(currentScreen = MypageContract.MypageScreen.MAIN) }
        } else if (currentScreen == MypageContract.MypageScreen.ALARM) {
            if (currentState.alarmScreenState == MypageContract.AlarmScreenState.SOUND_SETTING) {
                setState { copy(alarmScreenState = MypageContract.AlarmScreenState.MAIN) }
            } else if (currentState.alarmScreenState == MypageContract.AlarmScreenState.TIME_SETTING) {
                setState { copy(alarmScreenState = MypageContract.AlarmScreenState.MAIN) }
            } else {
                setState { copy(currentScreen = MypageContract.MypageScreen.MAIN) }
            }
        }
    }
}
