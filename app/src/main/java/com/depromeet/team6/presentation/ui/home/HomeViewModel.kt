package com.depromeet.team6.presentation.ui.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.domain.usecase.GetAddressFromCoordinatesUseCase
import com.depromeet.team6.domain.usecase.GetBusStartedUseCase
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.domain.usecase.GetCourseSearchResultsUseCase
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
    val getCourseSearchResultUseCase : GetCourseSearchResultsUseCase, // TODO : 실제 UseCase 로 교체
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

            is HomeContract.HomeEvent.LoadUserDeparture -> {
                setState {
                    copy(
                        userDeparture = event.userDeparture
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
                            departurePoint = it
                        )
                    }
                }.onFailure {
                    setState {
                        // 위치 찾을 수 없는 경우 서울시청으로 임의 초기화
                        copy(
                            departurePoint = Address(
                                name = "서울특별시청",
                                lat = 37.56681744674135,
                                lon = 126.97866075004276,
                                address = ""
                            )
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

    // TODO : 상세 경로 조회 API로 교체
    fun getLegs() {
        viewModelScope.launch {
            getCourseSearchResultUseCase(
                startPoint = currentState.departurePoint,
                endPoint = currentState.destinationPoint
            ).onSuccess { courseInfo ->
                setEvent(HomeContract.HomeEvent.LoadLegsResult(courseInfo[0]))
                setEvent(HomeContract.HomeEvent.LoadDepartureDateTime(courseInfo[0].departureTime))
                setEvent(
                    HomeContract.HomeEvent.LoadFirstTransportation(
                        getFirstTransportation(
                            courseInfo[0].legs
                        )
                    )
                )
            }
        }
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

    // TODO : 잠금화면에서 출발 클릭했을 때 호출
    fun setUserDeparture() {
        viewModelScope.launch {
            setEvent(HomeContract.HomeEvent.LoadUserDeparture(true))
        }
    }
}
