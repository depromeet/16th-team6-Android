package com.depromeet.team6.presentation.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.usecase.GetAddressFromCoordinatesUseCase
import com.depromeet.team6.domain.usecase.GetBusStartedUseCase
import com.depromeet.team6.domain.usecase.LoadMockSearchDataUseCase
import com.depromeet.team6.presentation.model.course.LegInfo
import com.depromeet.team6.presentation.model.course.TransportType
import com.depromeet.team6.presentation.ui.itinerary.ItineraryContract
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAddressFromCoordinatesUseCase: GetAddressFromCoordinatesUseCase,
    val loadMockData : LoadMockSearchDataUseCase, // TODO : 실제 UseCase 로 교체
    private val getBusStartedUseCase: GetBusStartedUseCase
) : BaseViewModel<HomeContract.HomeUiState, HomeContract.HomeSideEffect, HomeContract.HomeEvent>() {
    private var speechBubbleJob: Job? = null
    private var busStartedPollingJob: Job? = null
    private var lastRouteId: String = "" // TODO : 실제값으로 변경

    init {
        showSpeechBubbleTemporarily()
    }

    override fun createInitialState(): HomeContract.HomeUiState = HomeContract.HomeUiState()

    override suspend fun handleEvent(event: HomeContract.HomeEvent) {
        when (event) {
            is HomeContract.HomeEvent.DummyEvent -> setState { copy(loadState = event.loadState) }
            is HomeContract.HomeEvent.UpdateAlarmRegistered -> setState { copy(isAlarmRegistered = event.isRegistered) }
            is HomeContract.HomeEvent.UpdateBusDeparted -> setState { copy(isBusDeparted = event.isBusDeparted) }
            is HomeContract.HomeEvent.UpdateSpeechBubbleVisibility -> setState {
                copy(
                    showSpeechBubble = event.show
                )
            }

            is HomeContract.HomeEvent.OnCharacterClick -> onCharacterClick()
            is HomeContract.HomeEvent.LoadLegsResult -> {
                setState {
                    copy(
                        itineraryInfo = event.result,
                        courseDataLoadState = LoadState.Success
                    )
                }
            }
            is HomeContract.HomeEvent.LoadDepartureDateTime -> {
                setState {
                    copy(
                        departureTime = event.departureTime.substring(11,16)
                    )
                }
            }

            is HomeContract.HomeEvent.LoadFirstTransportation -> {
                setState {
                    copy(
                        firtTransportTation = event.transportation
                    )
                }
            }
        }
    }

    fun registerAlarm() {
        viewModelScope.launch {
            setEvent(HomeContract.HomeEvent.UpdateAlarmRegistered(true))

            when (currentState.firtTransportTation) {
                TransportType.BUS -> startPollingBusStarted(routeId = "") // TODO : routeId 변경
                TransportType.SUBWAY -> setEvent(HomeContract.HomeEvent.UpdateBusDeparted(true))
                else -> {}
            }
        }
    }

    fun finishAlarm(context: Context) {
        viewModelScope.launch {
            // TODO : 알림 등록 API 연동 후 SharedPreferences 로직 삭제
            val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            sharedPreferences.edit().apply {
                putBoolean("isUserLoggedIn", false)
                apply()
            }

            setEvent(HomeContract.HomeEvent.UpdateAlarmRegistered(false))
            setEvent(HomeContract.HomeEvent.UpdateBusDeparted(false))

            stopPollingBusStarted()
        }
    }

    fun setBusDeparted() {
        viewModelScope.launch {
            setEvent(HomeContract.HomeEvent.UpdateBusDeparted(true))
        }
    }

    fun getBusStarted(lastRouteId: String) {
        this.lastRouteId = lastRouteId
        viewModelScope.launch {
            getBusStartedUseCase.invoke(lastRouteId = lastRouteId)
                .onSuccess {
                    setState {
                        copy(
                            isBusDeparted = it
                        )
                    }

                    if (!it) {
                        stopPollingBusStarted()
                    }
                }.onFailure {
                    setState {
                        copy(
                            isBusDeparted = true
                        )
                    }
                    stopPollingBusStarted()
                }
        }
    }

    private fun showSpeechBubbleTemporarily() {
        speechBubbleJob?.cancel()

        speechBubbleJob = viewModelScope.launch {
            setEvent(HomeContract.HomeEvent.UpdateSpeechBubbleVisibility(true))
            delay(2500)
            setEvent(HomeContract.HomeEvent.UpdateSpeechBubbleVisibility(false))
        }
    }

    fun onCharacterClick() {
        showSpeechBubbleTemporarily()
    }

    fun getCenterLocation(location: LatLng) {
        viewModelScope.launch {
            getAddressFromCoordinatesUseCase.invoke(location.latitude, location.longitude)
                .onSuccess {
                    setState {
                        copy(
                            locationAddress = it.name
                        )
                    }
                }.onFailure {
                    setState {
                        copy(
                            locationAddress = ""
                        )
                    }
                }
        }
    }

    fun startPollingBusStarted(routeId: String) {
        busStartedPollingJob?.cancel()
        lastRouteId = routeId

        if (currentState.firtTransportTation == TransportType.BUS && currentState.isAlarmRegistered) {
            busStartedPollingJob = viewModelScope.launch {
                while (isActive) {
                    getBusStarted(routeId)

                    delay(60000)
                }
            }
        }
    }

    fun stopPollingBusStarted() {
        busStartedPollingJob?.cancel()
        busStartedPollingJob = null
    }

    // TODO : 실제 데이터로 교체
    fun getLegs() {
        val mockData = loadMockData()
        setEvent(HomeContract.HomeEvent.LoadLegsResult(mockData[0]))
        setEvent(HomeContract.HomeEvent.LoadDepartureDateTime(mockData[0].departureTime))
        setEvent(HomeContract.HomeEvent.LoadFirstTransportation(getFirstTransportation(mockData[0].legs)))
    }

    // 막차 경로 중 첫번째 대중 교통 수단
    private fun getFirstTransportation(legs: List<LegInfo>): TransportType {
        var firstTransportation = legs[0].transportType

        for (leg in legs) {
            if (leg.transportType != TransportType.WALK) {
                firstTransportation = leg.transportType
                break }
        }
        return firstTransportation
    }

    override fun onCleared() {
        super.onCleared()
        stopPollingBusStarted()
    }
}
