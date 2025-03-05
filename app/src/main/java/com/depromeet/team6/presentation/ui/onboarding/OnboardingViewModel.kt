package com.depromeet.team6.presentation.ui.onboarding

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.model.OnboardingSearchLocation
import com.depromeet.team6.domain.model.SignUp
import com.depromeet.team6.domain.usecase.DummyUseCase
import com.depromeet.team6.domain.usecase.PostSignUpUseCase
import com.depromeet.team6.presentation.type.OnboardingType
import com.depromeet.team6.presentation.util.Provider.KAKAO
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val dummyUseCase: DummyUseCase,
    val postSignUpUseCase: PostSignUpUseCase
) : BaseViewModel<OnboardingContract.OnboardingUiState, OnboardingContract.OnboardingSideEffect, OnboardingContract.OnboardingEvent>() {
    override fun createInitialState(): OnboardingContract.OnboardingUiState =
        OnboardingContract.OnboardingUiState()

    private val dummyLocations: List<OnboardingSearchLocation> = listOf(
        OnboardingSearchLocation(
            name = "60계 치킨 역삼점",
            distance = "300m",
            roadAddress = "서울 강남구 역삼동 696-18"
        ),
        OnboardingSearchLocation(
            name = "스타벅스 강남점",
            distance = "300m",
            roadAddress = "서울특별시 강남구 테헤란로 123"
        ),
        OnboardingSearchLocation(
            name = "이디야 커피 삼성점",
            distance = "450m",
            roadAddress = "서울특별시 강남구 삼성로 200"
        ),
        OnboardingSearchLocation(
            name = "파리바게뜨 역삼점",
            distance = "150m",
            roadAddress = "서울특별시 강남구 역삼로 55"
        ),
        OnboardingSearchLocation(
            name = "GS25 강남역점",
            distance = "100m",
            roadAddress = "서울특별시 강남구 강남대로 25"
        ),
        OnboardingSearchLocation(
            name = "세븐일레븐 선릉점",
            distance = "500m",
            roadAddress = "서울특별시 강남구 선릉로 88"
        )
    )

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
        }
    }

    private fun handleUpdateSearchText(event: OnboardingContract.OnboardingEvent.UpdateSearchText) {
        setState {
            copy(
                searchText = event.text,
                searchLocations = dummyLocations.filter { location ->
                    location.name.contains(event.text, ignoreCase = true) ||
                        location.roadAddress.contains(event.text, ignoreCase = true)
                }
            )
        }
    }

    fun postSignUp() {
        setEvent(OnboardingContract.OnboardingEvent.PostSignUp(loadState = LoadState.Loading))
        viewModelScope.launch {
            postSignUpUseCase(
                signUp = SignUp(
                    provider = KAKAO,
                    address = uiState.value.myHome.roadAddress,
                    lat = 127.036421,
                    lon = 37.500627,
                    alertFrequencies = uiState.value.alertFrequencies
                )
            ).onSuccess {
                setEvent(OnboardingContract.OnboardingEvent.PostSignUp(loadState = LoadState.Success))
            }.onFailure {
                setEvent(OnboardingContract.OnboardingEvent.PostSignUp(loadState = LoadState.Error))
            }
        }
    }
}
