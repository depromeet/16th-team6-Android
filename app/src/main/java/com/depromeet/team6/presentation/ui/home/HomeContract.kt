package com.depromeet.team6.presentation.ui.home

import com.depromeet.team6.presentation.model.course.LastTransportInfo
import com.depromeet.team6.presentation.model.course.TransportType
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class HomeContract {
    data class HomeUiState(
        val loadState: LoadState = LoadState.Idle,
        val isAlarmRegistered: Boolean = false,
        val isBusDeparted: Boolean = false,
        val showSpeechBubble: Boolean = true,
        val locationAddress: String = "",
        val logoutState: Boolean = false,
        // 알림 등록 후 경로 표시
        val itineraryInfo: LastTransportInfo? = null,
        val courseDataLoadState: LoadState = LoadState.Idle,
        val departureTime: String = "",
        // 막차 첫번째 교통 수단
        val firtTransportTation: TransportType = TransportType.WALK,
        // 사용자 출발 여부
        // TODO : 잠금화면에서 출발하기 버튼 클릭하면 해당 값 변경
        val userDeparture: Boolean = false
        val departurePoint: Address = Address(
            name = "성균관대학교 자연과학캠퍼스",
            lat = 37.303534788694,
            lon = 127.01085807594,
            address = ""
        ),
        val destinationPoint: Address = Address(
            name = "우리집",
            lat = 37.296391553347,
            lon = 126.97755824522,
            address = ""
        ),
        val logoutState: Boolean = false,
        val courseInfo: CourseInfo? = null
    ) : UiState

    sealed interface HomeSideEffect : UiSideEffect {
        data object NavigateToMypage : HomeSideEffect
        data object NavigateToItinerary : HomeSideEffect
    }

    sealed class HomeEvent : UiEvent {
        data class DummyEvent(val loadState: LoadState) : HomeEvent()
        data class UpdateAlarmRegistered(val isRegistered: Boolean) : HomeEvent()
        data class UpdateBusDeparted(val isBusDeparted: Boolean) : HomeEvent()
        data class UpdateSpeechBubbleVisibility(val show: Boolean) : HomeEvent()
        data class LoadLegsResult(val result: LastTransportInfo) : HomeEvent()
        data class LoadDepartureDateTime(val departureTime: String) : HomeEvent()
        data class LoadFirstTransportation(val transportation: TransportType) : HomeEvent()
        data class LoadUserDeparture(val userDeparture: Boolean) : HomeEvent()
        data object OnCharacterClick : HomeEvent()
    }
}
