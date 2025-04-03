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
            BusCourseContract.BusCourseEvent.RefreshButtonClicked -> {
                setEvent(BusCourseContract.BusCourseEvent.SetScreenLoadState(loadState = LoadState.Loading))
                getBusArrival()
            }
        }
    }

    fun initUiState(uiParameter: BusArrivalParameter) {
        setEvent(BusCourseContract.BusCourseEvent.SetScreenLoadState(loadState = LoadState.Loading))
        setState {
            copy(busArrivalParameter = uiParameter)
        }
        getBusArrival()
    }

    private fun getBusArrival() {
        val busArrivalParameter = currentState.busArrivalParameter
        viewModelScope.launch {
            getBusArrivalUseCase(
                routeName = currentState.busArrivalParameter.routeName,
                stationName = busArrivalParameter.stationName,
                lat = busArrivalParameter.lat,
                lon = busArrivalParameter.lon
            ).onSuccess { busArrival ->
                getBusPositions(
                    busRouteId = busArrival.busRouteId,
                    routeName = busArrival.routeName,
                    serviceRegion = busArrival.serviceRegion
                )
                val remainingTimes = busArrival.realTimeBusArrival.map { it.remainingTime }
                val busStatus = busArrival.realTimeBusArrival.map { it.busStatus }
                setState {
                    copy(
                        remainingTime = Pair(
                            remainingTimes.getOrNull(0) ?: -1,
                            remainingTimes.getOrNull(1) ?: -1
                        ),
                        busStatus = Pair(
                            busStatus[0],
                            busStatus[1]
                        ),
                        busPositionParameter = BusPositionParameter(
                            busRouteId = busArrival.busRouteId,
                            routeName = busArrival.routeName,
                            serviceRegion = busArrival.serviceRegion
                        ),
                        currentBusStationId = busArrival.busStationId,
                        busRouteName = busArrival.routeName
                    )
                }
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
                setState {
                    copy(
                        turnPoint = result.turnPoint,
                        busRouteStationList = result.busRouteStationList,
                        busPositions = result.busPositions
                    )
                }
                setEvent(BusCourseContract.BusCourseEvent.SetScreenLoadState(loadState = LoadState.Success))
            }.onFailure {
                setEvent(BusCourseContract.BusCourseEvent.SetScreenLoadState(loadState = LoadState.Error))
            }
        }
    }
}
