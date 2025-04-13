package com.depromeet.team6.presentation.ui.mypage

import android.content.Context
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.MypageUserInfo
import com.depromeet.team6.domain.model.UserInfo
import com.depromeet.team6.presentation.model.location.Location
import com.depromeet.team6.presentation.util.DefaultLntLng
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.android.gms.maps.model.LatLng

class MypageContract {
    enum class MypageScreen {
        MAIN, ACCOUNT, CHANGE_HOME, ALARM
    }

    enum class AlarmScreenState {
        MAIN, SOUND_SETTING, TIME_SETTING
    }

    enum class AlarmType {
        VIBRATION, SOUND
    }

    data class MypageUiState(
        val loadState: LoadState = LoadState.Idle,
        val currentScreen: MypageScreen = MypageScreen.MAIN,
        val isWebViewOpened: Boolean = false,
        val logoutDialogVisible: Boolean = false,
        val withDrawDialogVisible: Boolean = false,
        val searchText: String = "",
        val searchLocations: List<Location> = emptyList(),
        val searchPopupVisible: Boolean = false,
        val userCurrentLocation: LatLng = LatLng(DefaultLntLng.DEFAULT_LNT, DefaultLntLng.DEFAULT_LNG),
        val myAdress: Address = Address(
            name = "",
            lat = 0.0,
            lon = 0.0,
            address = ""
        ),
        val userInfo: MypageUserInfo = MypageUserInfo(
            nickname = "",
            profileImageUrl = "",
            address = "",
            lat = 0.0,
            lon = 0.0,
            alertFrequencies = emptySet(),
            fcmToken = ""
        ),
        val alertFrequencies: Set<Int> = setOf(1),
        val mapViewVisible: Boolean = false,
        val alarmScreenState: AlarmScreenState = AlarmScreenState.MAIN,
        val selectedAlarmType: AlarmType = AlarmType.SOUND
    ) : UiState

    sealed interface MypageSideEffect : UiSideEffect {
        data object NavigateBack : MypageSideEffect
        data object NavigateToLogin : MypageSideEffect
    }

    sealed class MypageEvent : UiEvent {
        data object BackPressed : MypageEvent()
        data object LogoutClicked : MypageEvent()
        data object WithDrawClicked : MypageEvent()
        data object PolicyClicked : MypageEvent()
        data object PolicyClosed : MypageEvent()
        data object LogoutConfirmed : MypageEvent()
        data object WithDrawConfirmed : MypageEvent()
        data object DismissDialog : MypageEvent()
        data object AccountClicked : MypageEvent()
        data object ChangeHomeClicked : MypageEvent()
        data object AlarmSettingClicked : MypageEvent()
        data class UpdateMyAddress(val myAdress: Address) : MypageEvent()
        data class ChangeMapViewVisible(val mapViewVisible: Boolean) : MypageEvent()
        data object ClearAddress : MypageEvent()
        data object ShowSearchPopup : MypageEvent()
        data object ClearText : MypageEvent()
        data class UpdateSearchText(val text: String) : MypageEvent()
        data object SearchPopUpBackPressed : MypageEvent()
        data class LocationSelectButtonClicked(val mypageSearchLocation: Address) : MypageEvent()
        data object SoundSettingClicked : MypageEvent()
        data object TimeSettingClicked : MypageEvent()
        data class AlarmTypeSelected(val type: AlarmType) : MypageEvent()
        data class UpdateAlertFrequencies(val alertFrequencies: Set<Int>) : MypageEvent()
    }
}