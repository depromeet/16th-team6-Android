package com.depromeet.team6.presentation.ui.onboarding

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.SignUp
import com.depromeet.team6.domain.repository.UserInfoRepository
import com.depromeet.team6.domain.usecase.GetAddressFromCoordinatesUseCase
import com.depromeet.team6.domain.usecase.GetLocationsUseCase
import com.depromeet.team6.domain.usecase.PostSignUpUseCase
import com.depromeet.team6.presentation.mapper.toPresentationList
import com.depromeet.team6.presentation.type.OnboardingType
import com.depromeet.team6.presentation.util.Provider.KAKAO
import com.depromeet.team6.presentation.util.Token.BEARER
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
class OnboardingViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val postSignUpUseCase: PostSignUpUseCase,
    private val getLocationsUseCase: GetLocationsUseCase,
    private val getAddressFromCoordinatesUseCase: GetAddressFromCoordinatesUseCase
) : BaseViewModel<OnboardingContract.OnboardingUiState, OnboardingContract.OnboardingSideEffect, OnboardingContract.OnboardingEvent>() {
    override fun createInitialState(): OnboardingContract.OnboardingUiState =
        OnboardingContract.OnboardingUiState()

    override suspend fun handleEvent(event: OnboardingContract.OnboardingEvent) {
        when (event) {
            is OnboardingContract.OnboardingEvent.PostSignUp -> setState { copy(loadState = event.loadState) }
            is OnboardingContract.OnboardingEvent.ChangeOnboardingType -> setState {
                copy(
                    onboardingType = OnboardingType.ALARM
                )
            }

            is OnboardingContract.OnboardingEvent.ClearText -> setState {
                copy(
                    searchText = "",
                    searchLocations = emptyList()
                )
            }

            is OnboardingContract.OnboardingEvent.ShowSearchPopup -> setState {
                copy(
                    searchPopupVisible = true
                )
            }

            is OnboardingContract.OnboardingEvent.UpdateSearchText -> handleUpdateSearchText(event = event)
            is OnboardingContract.OnboardingEvent.BackPressed -> setState { copy(onboardingType = OnboardingType.HOME) }
            is OnboardingContract.OnboardingEvent.LocationSelectButtonClicked -> setState {
                copy(
                    myAddress = event.onboardingSearchLocation,
                    searchPopupVisible = false
                )
            }

            is OnboardingContract.OnboardingEvent.UpdateAlertFrequencies -> setState {
                copy(alertFrequencies = event.alertFrequencies)
            }

            is OnboardingContract.OnboardingEvent.ChangePermissionBottomSheetVisible -> setState {
                copy(permissionBottomSheetVisible = event.permissionBottomSheetVisible)
            }

            is OnboardingContract.OnboardingEvent.UpdateUserLocation -> updateUserLocation(context = event.context)
            is OnboardingContract.OnboardingEvent.SearchPopUpBackPressed -> setState {
                copy(
                    searchPopupVisible = false
                )
            }

            is OnboardingContract.OnboardingEvent.ChangePermissionDeniedBottomSheetVisible -> setState {
                copy(permissionDeniedBottomSheetVisible = event.permissionDeniedBottomSheetVisible)
            }

            is OnboardingContract.OnboardingEvent.ClearAddress -> setState {
                copy(
                    myAddress = Address(
                        name = "",
                        lat = 0.0,
                        lon = 0.0,
                        address = ""
                    )
                )
            }

            is OnboardingContract.OnboardingEvent.ChangeMapViewVisible -> setState {
                copy(mapViewVisible = event.mapViewVisible)
            }
        }
    }

    private var debounceJob: Job? = null

    private fun handleUpdateSearchText(event: OnboardingContract.OnboardingEvent.UpdateSearchText) {
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

    fun postSignUp() {
        setEvent(OnboardingContract.OnboardingEvent.PostSignUp(loadState = LoadState.Loading))
        viewModelScope.launch {
            postSignUpUseCase(
                signUp = SignUp(
                    provider = KAKAO,
                    address = uiState.value.myAddress.name,
                    lat = uiState.value.myAddress.lat,
                    lon = uiState.value.myAddress.lon,
                    alertFrequencies = uiState.value.alertFrequencies,
                    fcmToken = userInfoRepository.getFcmToken()
                )
            ).onSuccess { auth ->
                setEvent(OnboardingContract.OnboardingEvent.PostSignUp(loadState = LoadState.Success))
                userInfoRepository.setAccessToken(BEARER + auth.accessToken)
                userInfoRepository.setRefreshToken(auth.refreshToken)
                userInfoRepository.setUserHome(auth.userHome)
            }.onFailure {
                setEvent(OnboardingContract.OnboardingEvent.PostSignUp(loadState = LoadState.Error))
            }
        }
    }

    private fun updateUserLocation(context: Context) {
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

    fun getCenterLocation(location: LatLng, onComplete: (Address) -> Unit = {}) {
        viewModelScope.launch {
            getAddressFromCoordinatesUseCase(location.latitude, location.longitude)
                .onSuccess { address ->
                    setState { copy(myAddress = address) }
                    onComplete(address)
                }
                .onFailure {
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
                    setState { copy(myAddress = address) }
                    onSuccess.invoke(address)
                }.onFailure {
                    Timber.e("주소 변환 실패: ${it.message}")
                }
        }
    }
}