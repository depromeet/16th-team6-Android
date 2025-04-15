package com.depromeet.team6.presentation.ui.bus

import com.depromeet.team6.domain.model.BusOperationInfo
import com.depromeet.team6.domain.model.BusPosition
import com.depromeet.team6.domain.model.BusRouteStation
import com.depromeet.team6.domain.model.BusStatus
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.depromeet.team6.presentation.model.bus.BusPositionParameter
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class BusCourseContract {
    data class BusCourseUiState(
        val loadState: LoadState = LoadState.Idle,
        val busArrivalParameter: BusArrivalParameter = BusArrivalParameter(
            routeName = "",
            stationName = "",
            lat = 0.0,
            lon = 0.0,
            subtypeIdx = 0
        ),
        val busRouteStationList: List<BusRouteStation> = emptyList(),
        val currentBusStationId: String = "",
        val turnPoint: Int = 0,
        val busPositions: List<BusPosition> = emptyList(),
        val busRouteName: String = "",
        val busPositionParameter: BusPositionParameter = BusPositionParameter(
            busRouteId = "",
            routeName = "",
            serviceRegion = ""
        ),
        val remainingTime: Pair<Int, Int> = Pair(0, 0),
        val busStatus: Pair<BusStatus, BusStatus> = Pair(BusStatus.WAITING, BusStatus.WAITING),
        val busOperationInfoVisible: Boolean = false,
        val busOperationInfo: BusOperationInfo = BusOperationInfo(
            startStationName = "",
            endStationName = "",
            serviceHours = emptyList()
        )
    ) : UiState

    sealed interface BusCourseSideEffect : UiSideEffect {
        data object NavigateToBackStack : BusCourseSideEffect
    }

    sealed class BusCourseEvent : UiEvent {
        data class SetScreenLoadState(val loadState: LoadState) : BusCourseEvent()
        data object RefreshButtonClicked : BusCourseEvent()
        data object ChangeBusOperationInfoVisible : BusCourseEvent()
    }
}
