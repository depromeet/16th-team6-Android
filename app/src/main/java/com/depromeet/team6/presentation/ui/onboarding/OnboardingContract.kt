package com.depromeet.team6.presentation.ui.onboarding

import com.depromeet.team6.domain.model.DummyData
import com.depromeet.team6.domain.model.OnboardingSearchLocation
import com.depromeet.team6.presentation.type.OnboardingType
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class OnboardingContract {
    data class OnboardingUiState(
        val loadState: LoadState = LoadState.Idle,
        val onboardingType: OnboardingType = OnboardingType.HOME,
        val searchPopupVisible: Boolean = false,
        val searchText: String = "",
        val dummyData: List<DummyData> = emptyList(),
        val myHome: OnboardingSearchLocation = OnboardingSearchLocation(
            name = "",
            distance = "",
            roadAddress = ""
        ),
        val searchLocations: List<OnboardingSearchLocation> = emptyList(),
        val alertFrequencies: Set<Int> = emptySet()
    ) : UiState

    sealed interface OnboardingSideEffect : UiSideEffect {
        data object DummySideEffect : OnboardingSideEffect
    }

    sealed class OnboardingEvent : UiEvent {
        data class PostSignUp(val loadState: LoadState) : OnboardingEvent()
        data object ShowSearchPopup : OnboardingEvent()
        data object ClearText : OnboardingEvent()
        data class UpdateSearchText(val text: String) : OnboardingEvent()
        data object ChangeOnboardingType : OnboardingEvent()
        data object BackPressed : OnboardingEvent()
        data class LocationSelectButtonClicked(val onboardingSearchLocation: OnboardingSearchLocation) : OnboardingEvent()
        data class UpdateAlertFrequencies(val alertFrequencies: Set<Int>) : OnboardingEvent()
    }
}
