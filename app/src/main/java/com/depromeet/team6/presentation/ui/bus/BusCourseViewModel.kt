package com.depromeet.team6.presentation.ui.bus

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.usecase.GetBusArrivalUseCase
import com.depromeet.team6.domain.usecase.GetBusPositionsUseCase
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.depromeet.team6.presentation.model.bus.BusPositionParameter
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusCourseViewModel @Inject constructor(
    private val getBusArrivalUseCase: GetBusArrivalUseCase,
    private val getBusPositionsUseCase: GetBusPositionsUseCase
) : BaseViewModel<BusCourseContract.BusCourseUiState, BusCourseContract.BusCourseSideEffect, BusCourseContract.BusCourseEvent>() {
    override fun createInitialState(): BusCourseContract.BusCourseUiState =
        BusCourseContract.BusCourseUiState()

    override suspend fun handleEvent(event: BusCourseContract.BusCourseEvent) {
        when (event) {
            is BusCourseContract.BusCourseEvent.SetScreenLoadState -> setState { copy(loadState = event.loadState) }
            BusCourseContract.BusCourseEvent.refreshButtonClicked -> {
                refreshBusPosition()
            }
        }
    }

    fun initUiState(uiParameter: BusArrivalParameter) {
        setState {
            copy(
                subtypeIdx = uiParameter.subtypeIdx
            )
        }
    }

    fun getBusArrival(routeName: String, stationName: String, lat: Double, lon: Double) {
        setEvent(BusCourseContract.BusCourseEvent.SetScreenLoadState(loadState = LoadState.Loading))
        viewModelScope.launch {
            getBusArrivalUseCase(
                routeName = routeName,
                stationName = stationName,
                lat = lat,
                lon = lon
            ).onSuccess { busArrival ->
                getBusPositions(
                    busRouteId = busArrival.busRouteId,
                    routeName = busArrival.routeName,
                    serviceRegion = busArrival.serviceRegion
                )
                setState {
                    copy(
                        busPositionParameter = BusPositionParameter(
                            busRouteId = busArrival.busRouteId,
                            routeName = busArrival.routeName,
                            serviceRegion = busArrival.serviceRegion
                        )
                    )
                }
                setState { copy(currentBusStationId = busArrival.busStationId) }
                setState { copy(busRouteName = busArrival.routeName) }
            }.onFailure {
                setEvent(BusCourseContract.BusCourseEvent.SetScreenLoadState(loadState = LoadState.Error))
            }
        }
    }

    private fun getBusPositions(
        busRouteId: String,
        routeName: String,
        serviceRegion: String
    ) {
        viewModelScope.launch {
            getBusPositionsUseCase(
                busRouteId = busRouteId,
                routeName = routeName,
                serviceRegion = serviceRegion
            ).onSuccess { result ->

                setState { copy(turnPoint = result.turnPoint) }
                setState { copy(busRouteStationList = result.busRouteStationList) }
                setState { copy(busPositions = result.busPositions) }
                setEvent(BusCourseContract.BusCourseEvent.SetScreenLoadState(loadState = LoadState.Success))
            }.onFailure {
                setEvent(BusCourseContract.BusCourseEvent.SetScreenLoadState(loadState = LoadState.Error))
            }
        }
    }

    private fun refreshBusPosition() {
        setEvent(BusCourseContract.BusCourseEvent.SetScreenLoadState(loadState = LoadState.Loading))
        viewModelScope.launch {
            getBusPositionsUseCase(
                busRouteId = currentState.busPositionParameter.busRouteId,
                routeName = currentState.busPositionParameter.routeName,
                serviceRegion = currentState.busPositionParameter.serviceRegion
            ).onSuccess { result ->
                setState { copy(turnPoint = result.turnPoint) }
                setState { copy(busRouteStationList = result.busRouteStationList) }
                setState { copy(busPositions = result.busPositions) }
                setEvent(BusCourseContract.BusCourseEvent.SetScreenLoadState(loadState = LoadState.Success))
            }.onFailure {
                setEvent(BusCourseContract.BusCourseEvent.SetScreenLoadState(loadState = LoadState.Error))
            }
        }
    }
}
