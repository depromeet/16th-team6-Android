package com.depromeet.team6.presentation.ui.onboarding

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.model.SignUp
import com.depromeet.team6.domain.repository.UserInfoRepository
import com.depromeet.team6.domain.usecase.GetLocationsUseCase
import com.depromeet.team6.domain.usecase.PostSignUpUseCase
import com.depromeet.team6.presentation.type.OnboardingType
import com.depromeet.team6.presentation.util.Provider.KAKAO
import com.depromeet.team6.presentation.util.Token.BEARER
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.context.getUserLocation
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.depromeet.team6.presentation.util.view.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val postSignUpUseCase: PostSignUpUseCase,
    private val getLocationsUseCase: GetLocationsUseCase
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

            is OnboardingContract.OnboardingEvent.ClearText -> setState { copy(searchText = "") }
            is OnboardingContract.OnboardingEvent.ShowSearchPopup -> setState {
                copy(
                    searchPopupVisible = true
                )
            }

            is OnboardingContract.OnboardingEvent.UpdateSearchText -> handleUpdateSearchText(event = event)
            is OnboardingContract.OnboardingEvent.BackPressed -> setState { copy(onboardingType = OnboardingType.HOME) }
            is OnboardingContract.OnboardingEvent.LocationSelectButtonClicked -> setState {
                copy(
                    myHome = event.onboardingSearchLocation,
                    searchPopupVisible = false
                )
            }

            is OnboardingContract.OnboardingEvent.UpdateAlertFrequencies -> setState {
                copy(alertFrequencies = event.alertFrequencies)
            }

            is OnboardingContract.OnboardingEvent.UpdateUserLocation -> updateUserLocation(context = event.context)
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
                            searchLocations = locations
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
                    address = uiState.value.myHome.address,
                    lat = 127.036421,
                    lon = 37.500627,
                    alertFrequencies = uiState.value.alertFrequencies,
                    fcmToken = userInfoRepository.getFcmToken()
                )
            ).onSuccess { auth ->
                setEvent(OnboardingContract.OnboardingEvent.PostSignUp(loadState = LoadState.Success))
                userInfoRepository.setAccessToken(BEARER + auth.accessToken)
                userInfoRepository.setRefreshToken(auth.refreshToken)
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
}
