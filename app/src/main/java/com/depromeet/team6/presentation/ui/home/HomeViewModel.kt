package com.depromeet.team6.presentation.ui.home

import android.content.Context
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
import com.depromeet.team6.domain.usecase.GetBusArrivalUseCase
import com.depromeet.team6.domain.usecase.GetBusStartedUseCase
import com.depromeet.team6.domain.usecase.GetCourseSearchResultsUseCase
import com.depromeet.team6.domain.usecase.GetTaxiCostUseCase
import com.depromeet.team6.domain.usecase.GetUserInfoUseCase
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.depromeet.team6.presentation.util.AmplitudeCommon.SCREEN_NAME
import com.depromeet.team6.presentation.util.AmplitudeCommon.USER_ID
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_EVENT_ITINERARY_BTN_CLICK
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_EVENT_REGISTER_MAP_MARKER_CLICK
import com.depromeet.team6.presentation.util.HomeAmplitude.REGISTER_MAP_MARKER_CLICKED
import com.depromeet.team6.presentation.util.amplitude.AmplitudeUtils
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val getAddressFromCoordinatesUseCase: GetAddressFromCoordinatesUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getTaxiCostUseCase: GetTaxiCostUseCase,
    val getCourseSearchResultUseCase: GetCourseSearchResultsUseCase,
    private val getBusStartedUseCase: GetBusStartedUseCase,
    private val getBusArrivalUseCase: GetBusArrivalUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase
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

            is HomeContract.HomeEvent.LoadHomeArrivedTime -> {
                setState {
                    copy(
                        homeArrivedTime = event.homeArrivedTime
                    )
                }
            }

            is HomeContract.HomeEvent.LoadBusArrivalParameter -> {
                setState {
                    copy(
                        busArrivalParameter = event.busArrivalParameter
                    )
                }
            }

            is HomeContract.HomeEvent.AfterRegisterMapMarkerClick -> {
                AmplitudeUtils.trackEventWithProperties(
                    eventName = HOME_EVENT_REGISTER_MAP_MARKER_CLICK,
                    properties = mapOf(
                        SCREEN_NAME to HOME,
                        USER_ID to userInfoRepository.getUserID(),
                        REGISTER_MAP_MARKER_CLICKED to 1
                    )
                )
            }
            is HomeContract.HomeEvent.CourseDetailButtonClick -> {
                AmplitudeUtils.trackEventWithProperties(
                    eventName = HOME_EVENT_ITINERARY_BTN_CLICK,
                    properties = mapOf(
                        SCREEN_NAME to HOME,
                        USER_ID to userInfoRepository.getUserID(),
                        event.clickEventKey to 1
                    )
                )
            }

            is HomeContract.HomeEvent.ChangeGreetBottomSheetVisible -> setState {
                copy(greetBottomSheetVisible = event.visible)
            }

            HomeContract.HomeEvent.CharacterClicked -> handleCharacterClick()
            // is HomeContract.HomeEvent.ComponentClicked -> handleComponentClick(event.componentType, event.data)
            is HomeContract.HomeEvent.ComponentClicked -> TODO()
        }
    }

    fun getUserId(): Int {
        return userInfoRepository.getUserID()
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
            deleteAlarmUseCase(
                lastRouteId = lastRouteId
            )
                .onSuccess {
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
                    setSideEffect(HomeContract.HomeSideEffect.ShowDeleteAlarmToast)
                }
                .onFailure { exception ->
                    handleApiException(exception)
                }
        }
    }

    private fun getBusStarted(lastRouteId: String) {
        this.lastRouteId = lastRouteId
        viewModelScope.launch {
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
                    handleApiException(it) {
                        copy(
                            isBusDeparted = false
                        )
                    }
                    // TODO : 실패 5번 누적되면 푸시알림 구현
                    Timber.e("버스 출발여부 에러: ${it.message}")
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

    private fun onCharacterClick() {
        showSpeechBubbleTemporarily()
    }

    fun getCenterLocation(location: LatLng) {
        viewModelScope.launch {
            getAddressFromCoordinatesUseCase(location.latitude, location.longitude)
                .onSuccess { addressData ->
                    val newMarkerPoint = if (addressData.name.isEmpty()) {
                        addressData.copy(name = addressData.address)
                    } else {
                        addressData
                    }
                    setState {
                        copy(markerPoint = newMarkerPoint)
                    }

                    val isAllNotDefault = newMarkerPoint.lat != DEFAULT_MARKER_LAT &&
                            newMarkerPoint.lon != DEFAULT_MARKER_LON &&
                            currentState.destinationPoint.lat != DEFAULT_DESTINATION_LAT &&
                            currentState.destinationPoint.lon != DEFAULT_DESTINATION_LON

                    if (isAllNotDefault) {
                        getTaxiCost()
                    }
                    getTaxiCost()
                }
                .onFailure { exception ->
                    handleApiException(exception)
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

    // SharedPreferences에서 사용자 출발 상태를 로드
    fun loadUserDepartureState(context: Context) {
        viewModelScope.launch {
            val sharedPreferences = context.getSharedPreferences(MY_PREFERENCES_NAME, Context.MODE_PRIVATE)
            val userDeparture = sharedPreferences.getBoolean("userDeparture", false)
            setEvent(HomeContract.HomeEvent.LoadUserDeparture(userDeparture))
            if (currentState.userDeparture && currentState.firtTransportTation == TransportType.BUS) {
                getBusArrival()
                Timber.e("busArrivalParameter", currentState.busArrivalParameter.toString())
            }
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
                    setEvent(HomeContract.HomeEvent.LoadHomeArrivedTime(calculateArrivalTime(courseInfo.departureTime, courseInfo.totalTime)))
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

    private fun getBusArrivalParameter(leg: LegInfo) {
        setEvent(
            HomeContract.HomeEvent.LoadBusArrivalParameter(
                BusArrivalParameter(
                    routeName = leg.routeName!!,
                    stationName = leg.startPoint.name,
                    lat = leg.startPoint.lat,
                    lon = leg.startPoint.lon,
                    subtypeIdx = 0
                )
            )
        )
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
                    getBusArrivalParameter(leg)
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

    private fun getTaxiCost() {
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
                AmplitudeUtils.setUserId(userId = userInfo.id)
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

                val isAllNotDefault = currentState.markerPoint.lat != DEFAULT_MARKER_LAT &&
                        currentState.markerPoint.lon != DEFAULT_MARKER_LON &&
                        userInfo.userHome.latitude != DEFAULT_DESTINATION_LAT &&
                        userInfo.userHome.longitude != DEFAULT_DESTINATION_LON

                if (isAllNotDefault) {
                    getTaxiCost()
                }
                getTaxiCost()

            }.onFailure { exception ->
                handleApiException(exception)
            }
        }
    }

    private fun calculateArrivalTime(departureDateTime: String, totalTimeInSeconds: Int): String {
        try {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val departure = LocalDateTime.parse(departureDateTime, formatter)

            val arrival = departure.plusSeconds(totalTimeInSeconds.toLong())

            val outputFormatter = DateTimeFormatter.ofPattern("HH:mm")
            return arrival.format(outputFormatter)
        } catch (e: Exception) {
            return "날짜 변환 중 오류 발생: ${e.message}"
        }
    }

    fun getBusArrival() {
        viewModelScope.launch {
            getBusArrivalUseCase(
                routeName = currentState.busArrivalParameter.routeName,
                stationName = currentState.busArrivalParameter.stationName,
                lat = currentState.busArrivalParameter.lat,
                lon = currentState.busArrivalParameter.lon
            ).onSuccess { busArrival ->
                setState {
                    copy(
                        busRemainingStations = busArrival.realTimeBusArrival[0].remainingStations
                    )
                }
                setState {
                    copy(
                        boardingTime = busArrival.realTimeBusArrival[0].remainingTime.toString()
                    )
                }
            }.onFailure { exception ->
                handleApiException(exception = exception)
            }
        }
    }

    fun updateCurrentLocation(newLocation: LatLng) {
        viewModelScope.launch {
            setState {
                copy(
                    currentLocation = newLocation
                )
            }
        }
    }

    private fun handleCharacterClick() {
        val currentState = uiState.value.characterState
        val speechTexts = currentState.speechTexts

        if (speechTexts.size > 1) {
            val nextIndex = (currentState.currentSpeechIndex + 1) % speechTexts.size
            setState {
                copy(
                    characterState = currentState.copy(
                        currentSpeechIndex = nextIndex,
                        isAnimating = true,
                        animationTrigger = currentState.animationTrigger + 1
                    )
                )
            }
        } else {
            setState {
                copy(
                    characterState = currentState.copy(
                        isAnimating = true,
                        animationTrigger = currentState.animationTrigger + 1
                    )
                )
            }
        }
    }

    companion object {
        private const val MY_PREFERENCES_NAME = "MyPreferences"
        private const val DEFAULT_MARKER_LAT = 37.303534788694
        private const val DEFAULT_MARKER_LON = 127.01085807594
        private const val DEFAULT_DESTINATION_LAT = 37.296391553347
        private const val DEFAULT_DESTINATION_LON = 126.97755824522
    }
}
