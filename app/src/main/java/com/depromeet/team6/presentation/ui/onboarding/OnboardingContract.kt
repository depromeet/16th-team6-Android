package com.depromeet.team6.presentation.ui.onboarding

import android.content.Context
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.DummyData
import com.depromeet.team6.presentation.model.location.Location
import com.depromeet.team6.presentation.type.OnboardingType
import com.depromeet.team6.presentation.util.DefaultLntLng.DEFAULT_LNG
import com.depromeet.team6.presentation.util.DefaultLntLng.DEFAULT_LNT
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.android.gms.maps.model.LatLng

class OnboardingContract {
    data class OnboardingUiState(
        val loadState: LoadState = LoadState.Idle,
        val onboardingType: OnboardingType = OnboardingType.HOME,
        val searchPopupVisible: Boolean = false,
        val searchText: String = "",
        val dummyData: List<DummyData> = emptyList(),
//        val myHome: Location = Location(
//            name = "",
//            lat = 0.0,
//            lon = 0.0,
//            businessCategory = "",
//            address = "",
//            radius = ""
//        ),
        val searchLocations: List<Location> = emptyList(),
        var userCurrentLocation: LatLng = LatLng(DEFAULT_LNT, DEFAULT_LNG),
        val alertFrequencies: Set<Int> = setOf(1),
        var permissionBottomSheetVisible: Boolean = false,
        var permissionDeniedBottomSheetVisible: Boolean = false,
        val myAddress: Address = Address(
            name = "",
            lat = 0.0,
            lon = 0.0,
            address = ""
        )
    ) : UiState

    sealed interface OnboardingSideEffect : UiSideEffect {
        data object DummySideEffect : OnboardingSideEffect
    }

    sealed class OnboardingEvent : UiEvent {
        data class PostSignUp(val loadState: LoadState) : OnboardingEvent()
        data object ShowSearchPopup : OnboardingEvent()
        data object ClearText : OnboardingEvent()
        data class UpdateSearchText(val text: String) :
            OnboardingEvent()

        data object ChangeOnboardingType : OnboardingEvent()
        data object BackPressed : OnboardingEvent()
        data class LocationSelectButtonClicked(val onboardingSearchLocation: Address) :
            OnboardingEvent()

        data class UpdateAlertFrequencies(val alertFrequencies: Set<Int>) : OnboardingEvent()
        data class ChangePermissionBottomSheetVisible(val permissionBottomSheetVisible: Boolean) : OnboardingEvent()
        data class UpdateUserLocation(val context: Context) : OnboardingEvent()
        data object SearchPopUpBackPressed : OnboardingEvent()
        data class ChangePermissionDeniedBottomSheetVisible(val permissionDeniedBottomSheetVisible: Boolean) : OnboardingEvent()
    }
}
