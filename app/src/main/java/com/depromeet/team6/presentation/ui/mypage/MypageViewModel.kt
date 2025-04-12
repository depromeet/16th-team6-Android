package com.depromeet.team6.presentation.ui.mypage

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.data.repositoryimpl.UserInfoRepositoryImpl
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.usecase.DeleteWithDrawUseCase
import com.depromeet.team6.domain.usecase.GetAddressFromCoordinatesUseCase
import com.depromeet.team6.domain.usecase.GetLocationsUseCase
import com.depromeet.team6.domain.usecase.GetUserInfoUseCase
import com.depromeet.team6.domain.usecase.PostLogoutUseCase
import com.depromeet.team6.domain.usecase.UpdateUserAddressUseCase
import com.depromeet.team6.presentation.mapper.toPresentationList
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.context.getUserLocation
import com.depromeet.team6.presentation.util.permission.PermissionUtil
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
    private val updateUserAddressUseCase: UpdateUserAddressUseCase
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
        }
    }

    fun getUserInfo() {
        if (isAddressInitialized && currentState.myAdress.address.isNotEmpty()) {
            Timber.d("주소가 이미 초기화되어 있어 getUserInfo에서 주소를 갱신하지 않습니다.")
            return
        }

        viewModelScope.launch {
            getUserInfoUseCase().onSuccess { userInfo ->
                setState {
                    copy(
                        myAdress = Address(
                            name = userInfo.address,
                            lat = userInfo.userHome.latitude,
                            lon = userInfo.userHome.longitude,
                            address = userInfo.address
                        )
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

    fun setCurrentLocationToHomeAddress(
        context: Context,
        onSuccess: (Address) -> Unit = {}
    ) {
        viewModelScope.launch {
            val location = context.getUserLocation()
            setState { copy(userCurrentLocation = location) }

            getAddressFromCoordinatesUseCase.invoke(location.latitude, location.longitude)
                .onSuccess { address ->
                    setState { copy(myAdress = address) }
                    onSuccess.invoke(address)
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

//    fun updateUserAddress() {
//        viewModelScope.launch {
//            val address = currentState.myAdress
//            updateUserAddressUseCase(
//                address = address.name,
//                lat = address.lat,
//                lon = address.lon
//            ).onSuccess {
//                // 저장 성공
//                userInfoRepositoryImpl.updateUserAddress(address.lat, address.lon)
//                setState { copy(loadState = LoadState.Success) }
//
//                // 메인 화면으로 이동
//                setState { copy(currentScreen = MypageContract.MypageScreen.MAIN) }
//            }.onFailure {
//                Timber.e("주소 업데이트 실패: ${it.message}")
//                setState { copy(loadState = LoadState.Error) }
//            }
//        }
//    }

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

    private fun navigateBack() {
        val currentScreen = currentState.currentScreen
        if (currentScreen == MypageContract.MypageScreen.MAIN) {
            setSideEffect(MypageContract.MypageSideEffect.NavigateBack)
        } else if (currentScreen == MypageContract.MypageScreen.ACCOUNT) {
            setState { copy(currentScreen = MypageContract.MypageScreen.MAIN) }
        } else if (currentScreen == MypageContract.MypageScreen.CHANGE_HOME) {
            setState { copy(currentScreen = MypageContract.MypageScreen.MAIN) }
        }
    }
}