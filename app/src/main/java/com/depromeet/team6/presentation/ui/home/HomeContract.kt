package com.depromeet.team6.presentation.ui.home

import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class HomeContract {
    data class HomeUiState(
        val loadState: LoadState = LoadState.Idle,
        val destinationState: LoadState = LoadState.Idle,
        val isAlarmRegistered: Boolean = false,
        val isBusDeparted: Boolean = false,
        val showSpeechBubble: Boolean = true,
        val locationAddress: String = "",
        // 알림 등록 후 경로 표시
        val itineraryInfo: CourseInfo? = null,
        val courseDataLoadState: LoadState = LoadState.Idle,
        val departureTime: String = "", // 사용자 출발 시간
        val boardingTime: String = "", // 막차 출발 시간
        val lastRouteId: String = "",
        // 막차 첫번째 교통 수단
        val firtTransportTation: TransportType = TransportType.WALK,
        val firstTransportationNumber: Int = 0,
        val firstTransportationName: String = "",
        // 사용자 출발 여부
        val userDeparture: Boolean = false,
        // 타이머 시간이 끝났을 때
        val timerFinish: Boolean = false,
        val departurePointName: String = "",
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
        val courseInfo: CourseInfo? = null,
        val taxiCost: Int = 0,
        val deleteAlarmDialogVisible: Boolean = false
    ) : UiState

    sealed interface HomeSideEffect : UiSideEffect {
        data object NavigateToMypage : HomeSideEffect
        data object NavigateToItinerary : HomeSideEffect
    }

    sealed class HomeEvent : UiEvent {
        data class DummyEvent(val loadState: LoadState) : HomeEvent()
        data class UpdateAlarmRegistered(val isRegistered: Boolean) : HomeEvent()
        data class UpdateLastRouteId(val lastRouteId: String) : HomeEvent()
        data class UpdateDeparturePointName(val departurePointName: String) : HomeEvent()
        data class UpdateBusDeparted(val isBusDeparted: Boolean) : HomeEvent()
        data class UpdateSpeechBubbleVisibility(val show: Boolean) : HomeEvent()
        data class LoadLegsResult(val result: CourseInfo) : HomeEvent()
        data class LoadDepartureDateTime(val departureTime: String) : HomeEvent()
        data class LoadBoardingDateTime(val boardingTime: String) : HomeEvent()
        data class LoadFirstTransportation(val transportation: TransportType) : HomeEvent()
        data class LoadFirstTransportationNumber(val firstTransportationNumber: Int) : HomeEvent()
        data class LoadFirstTransportationName(val firstTransportationName: String) : HomeEvent()
        data class LoadUserDeparture(val userDeparture: Boolean) : HomeEvent()
        data class LoadTimerFinish(val timerFinish: Boolean) : HomeEvent()
        data object OnCharacterClick : HomeEvent()
        data object FinishAlarmClicked : HomeEvent()
        data object DeleteAlarmConfirmed : HomeEvent()
        data object DismissDialog : HomeEvent()
        data object SetDestination : HomeEvent()
    }
}
