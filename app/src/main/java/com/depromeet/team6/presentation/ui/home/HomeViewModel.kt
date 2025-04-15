package com.depromeet.team6.presentation.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.data.datalocal.manager.LockServiceManager
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.RouteLocation
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.domain.repository.UserInfoRepository
import com.depromeet.team6.domain.usecase.DeleteAlarmUseCase
import com.depromeet.team6.domain.usecase.GetAddressFromCoordinatesUseCase
import com.depromeet.team6.domain.usecase.GetBusStartedUseCase
import com.depromeet.team6.domain.usecase.GetCourseSearchResultsUseCase
import com.depromeet.team6.domain.usecase.GetTaxiCostUseCase
import com.depromeet.team6.domain.usecase.GetUserInfoUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAddressFromCoordinatesUseCase: GetAddressFromCoordinatesUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getTaxiCostUseCase: GetTaxiCostUseCase,
    val getCourseSearchResultUseCase: GetCourseSearchResultsUseCase,
    private val getBusStartedUseCase: GetBusStartedUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val userInfoRepository: UserInfoRepository
    ) : BaseViewModel<HomeContract.HomeUiState, HomeContract.HomeSideEffect, HomeContract.HomeEvent>() {
    private var speechBubbleJob: Job? = null
    private var busStartedPollingJob: Job? = null
    private var lastRouteId: String = ""

    @Inject
    lateinit var lockServiceManager: LockServiceManager

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
            is HomeContract.HomeEvent.SetDestination -> setDestination()
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
                        departureTime = event.departureTime.substring(11, 16)
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

            is HomeContract.HomeEvent.LoadTimerFinish -> {
                setState {
                    copy(
                        timerFinish = event.timerFinish
                    )
                }
            }

            is HomeContract.HomeEvent.UpdateLastRouteId -> {
                setState {
                    copy(
                        lastRouteId = event.lastRouteId
                    )
                }
            }

            is HomeContract.HomeEvent.LoadFirstTransportationNumber -> {
                setState {
                    copy(
                        firstTransportationNumber = event.firstTransportationNumber
                    )
                }
            }

            is HomeContract.HomeEvent.LoadFirstTransportationName -> {
                setState {
                    copy(
                        firstTransportationName = event.firstTransportationName
                    )
                }
            }

            is HomeContract.HomeEvent.LoadBoardingDateTime -> {
                setState {
                    copy(
                        boardingTime = event.boardingTime
                    )
                }
            }

            is HomeContract.HomeEvent.UpdateDeparturePointName -> {
                setState {
                    copy(
                        departurePointName = event.departurePointName
                    )
                }
            }
            HomeContract.HomeEvent.DeleteAlarmConfirmed -> setState {
                copy(
                    deleteAlarmDialogVisible = false
                )
            }
            HomeContract.HomeEvent.DismissDialog -> setState {
                copy(
                    deleteAlarmDialogVisible = false
                )
            }
            HomeContract.HomeEvent.FinishAlarmClicked -> {
                setState { copy(deleteAlarmDialogVisible = true) }
            }

            is HomeContract.HomeEvent.UpdateDeparturePoint -> setState {
                copy(
                    departurePoint = event.departurePoint
                )
            }
        }
    }

    fun onTimerFinished() {
        setState {
            copy(
                timerFinish = true
            )
        }
    }

    fun registerAlarm() {
        viewModelScope.launch {
            setEvent(HomeContract.HomeEvent.UpdateAlarmRegistered(true))
        }
    }

    fun deleteAlarm(lastRouteId: String, context: Context) {
        viewModelScope.launch {
            if (deleteAlarmUseCase(
                    lastRouteId = lastRouteId
                ).isSuccessful
            ) {
                setEvent(HomeContract.HomeEvent.UpdateAlarmRegistered(false))
                setEvent(HomeContract.HomeEvent.UpdateBusDeparted(false))

                stopPollingBusStarted()

                val sharedPreferences = context.getSharedPreferences(
                    "MyPreferences",
                    Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()

                editor.remove("departurePoint")
                editor.remove("lastCourseInfo")
                editor.remove("lastRouteId")
                editor.remove("alarmRegistered")
                editor.remove("userDeparture")

                editor.apply()

                setEvent(HomeContract.HomeEvent.DismissDialog)
            } else {
                Log.d("알림 삭제 실패", "알림 삭제 실패")
            }
        }
    }

    private fun getBusStarted(lastRouteId: String) {
        this.lastRouteId = lastRouteId
        viewModelScope.launch {
            try {
                getBusStartedUseCase.invoke(lastRouteId = lastRouteId)
                    .onSuccess {
                        Timber.d("버스 출발여부 getBusStarted: $it")
                        setState {
                            copy(
                                isBusDeparted = it
                            )
                        }
                        if (it) {
                            stopPollingBusStarted()
                        }
                    }.onFailure {
                        Timber.e("버스 출발여부 에러: ${it.message}")
                        setState {
                            copy(
                                isBusDeparted = true
                            )
                        }
                        stopPollingBusStarted()
                    }
            } catch (e: Exception) {
                Timber.e("예상치 못한 예외 발생: ${e.message}")
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
        getTaxiCost()
    }

    fun getCenterLocation(location: LatLng) {
        viewModelScope.launch {
            getAddressFromCoordinatesUseCase.invoke(location.latitude, location.longitude)
                .onSuccess { addressData ->
                    setState {
                        copy(
                            markerPoint = if (addressData.name.isEmpty()) {
                                addressData.copy(name = addressData.address)
                            } else {
                                addressData
                            }
                        )
                    }
                    getTaxiCost()
                }.onFailure {
                    setState {
                        // 위치 찾을 수 없는 경우 서울시청으로 임의 초기화
                        copy(
                            markerPoint = Address(
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
                    Timber.d("버스 차고지 출발 여부 API 호출")
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

    // SharedPreferences에서 사용자 출발 상태를 로드
    fun loadUserDepartureState(context: Context) {
        viewModelScope.launch {
            val sharedPreferences = context.getSharedPreferences(MY_PREFERENCES_NAME, Context.MODE_PRIVATE)
            val userDeparture = sharedPreferences.getBoolean("userDeparture", false)
            setEvent(HomeContract.HomeEvent.LoadUserDeparture(userDeparture))
        }
    }

    fun loadAlarmAndCourseInfoFromPrefs(context: Context) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences(MY_PREFERENCES_NAME, Context.MODE_PRIVATE)
            val isAlarmRegistered = prefs.getBoolean("alarmRegistered", false)
            val lastRouteId = prefs.getString("lastRouteId", null)

            setEvent(HomeContract.HomeEvent.UpdateAlarmRegistered(isAlarmRegistered))
            lastRouteId?.let { HomeContract.HomeEvent.UpdateLastRouteId(it) }?.let { setEvent(it) }

            val departurePointJson = prefs.getString("departurePoint", null)
            departurePointJson?.let {
                try {
                    val departurePoint = Gson().fromJson(it, Address::class.java)
                    setEvent(HomeContract.HomeEvent.UpdateDeparturePointName(departurePoint.name))
                    setEvent(HomeContract.HomeEvent.UpdateDeparturePoint(departurePoint))
                } catch (e: Exception) {
                    Timber.e("DeparturePoint 불러오기 실패: ${e.message}")
                    e.printStackTrace()
                }
            }
            val courseInfoJson = prefs.getString("lastCourseInfo", null)
            courseInfoJson?.let {
                try {
                    val courseInfo = Gson().fromJson(it, CourseInfo::class.java)
                    setEvent(HomeContract.HomeEvent.LoadLegsResult(courseInfo))
                    setEvent(HomeContract.HomeEvent.LoadDepartureDateTime(courseInfo.departureTime))
                    setEvent(HomeContract.HomeEvent.LoadBoardingDateTime(courseInfo.boardingTime))
                    setEvent(HomeContract.HomeEvent.LoadFirstTransportation(getFirstTransportation(courseInfo.legs)))
                    setEvent(HomeContract.HomeEvent.LoadFirstTransportationNumber(getFirstTransportationNumber(courseInfo.legs)))
                    setEvent(HomeContract.HomeEvent.LoadFirstTransportationName(getFirstTransportationName(courseInfo.legs)))

                    if (getFirstTransportation(courseInfo.legs) == TransportType.BUS) {
                        startPollingBusStarted(lastRouteId!!)
                    } else if (getFirstTransportation(courseInfo.legs) == TransportType.SUBWAY) {
                        setEvent(HomeContract.HomeEvent.UpdateBusDeparted(true))
                    }
                } catch (e: Exception) {
                    Timber.e("CourseInfo 불러오기 실패: ${e.message}")
                }
            }
        }
    }

    private fun getFirstTransportation(legs: List<LegInfo>): TransportType {
        var firstTransportation = legs[0].transportType

        for (leg in legs) {
            if (leg.transportType != TransportType.WALK) {
                firstTransportation = leg.transportType
                break
            }
        }
        return firstTransportation
    }

    private fun getFirstTransportationNumber(legs: List<LegInfo>): Int {
        var firstTransportationNumber = 0

        for (leg in legs) {
            if (leg.transportType != TransportType.WALK) {
                firstTransportationNumber = leg.subTypeIdx
                break
            }
        }
        return firstTransportationNumber
    }

    private fun getFirstTransportationName(legs: List<LegInfo>): String {
        var firstTransportationName = ""

        for (leg in legs) {
            if (leg.transportType != TransportType.WALK) {
                if (leg.transportType == TransportType.BUS) {
                    firstTransportationName = leg.routeName.toString().split(":")[1]
                } else if (leg.transportType == TransportType.SUBWAY) {
                    firstTransportationName = leg.startPoint.name + "역"
                }
                break
            }
        }
        return firstTransportationName
    }

    override fun onCleared() {
        super.onCleared()
        stopPollingBusStarted()
    }

    fun getTaxiCost() {
        viewModelScope.launch {
            getTaxiCostUseCase(
                routeLocation = RouteLocation(
                    startLat = currentState.markerPoint.lat,
                    startLon = currentState.markerPoint.lon,
                    endLat = currentState.destinationPoint.lat,
                    endLon = currentState.destinationPoint.lon
                )
            )
                .onSuccess {
                    setState {
                        copy(
                            taxiCost = it
                        )
                    }
                    getTaxiCostUseCase.saveTaxiCost(it)
                }.onFailure {
                    setState {
                        copy(
                            taxiCost = 0
                        )
                    }
                }
        }
    }

    private fun setDestination() {
        setState { copy(destinationState = LoadState.Loading) }
        viewModelScope.launch {
            getUserInfoUseCase().onSuccess { userInfo ->
                userInfoRepository.setUserId(userId = userInfo.id)
                setState {
                    copy(
                        destinationPoint = Address(
                            name = userInfo.address,
                            lat = userInfo.userHome.latitude,
                            lon = userInfo.userHome.longitude,
                            address = userInfo.address
                        )
                    )
                }
                setState { copy(destinationState = LoadState.Success) }
            }.onFailure {
                setState { copy(destinationState = LoadState.Error) }
            }
        }
    }

    fun getUserId(): Int {
        return userInfoRepository.getUserID()
    }

    companion object {
        private const val MY_PREFERENCES_NAME = "MyPreferences"
    }
}
