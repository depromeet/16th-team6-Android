package com.depromeet.team6.presentation.ui.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.data.background.AlarmScheduler
import com.depromeet.team6.data.background.LockServiceManager
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.RouteLocation
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.domain.repository.HomeRepository
import com.depromeet.team6.domain.repository.UserInfoRepository
import com.depromeet.team6.domain.usecase.DeleteAlarmUseCase
import com.depromeet.team6.domain.usecase.GetAddressFromCoordinatesUseCase
import com.depromeet.team6.domain.usecase.GetBusArrivalUseCase
import com.depromeet.team6.domain.usecase.GetBusStartedUseCase
import com.depromeet.team6.domain.usecase.GetCourseSearchResultsUseCase
import com.depromeet.team6.domain.usecase.GetTaxiCostUseCase
import com.depromeet.team6.domain.usecase.GetUserInfoUseCase
import com.depromeet.team6.domain.usecase.RefreshAlarmTimerUseCase
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.depromeet.team6.presentation.util.AmplitudeCommon.SCREEN_NAME
import com.depromeet.team6.presentation.util.AmplitudeCommon.USER_ID
import com.depromeet.team6.presentation.util.DefaultMarkerDestination.DEFAULT_DESTINATION_LAT
import com.depromeet.team6.presentation.util.DefaultMarkerDestination.DEFAULT_DESTINATION_LON
import com.depromeet.team6.presentation.util.DefaultMarkerDestination.DEFAULT_MARKER_LAT
import com.depromeet.team6.presentation.util.DefaultMarkerDestination.DEFAULT_MARKER_LON
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_EVENT_ITINERARY_BTN_CLICK
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_EVENT_REGISTER_MAP_MARKER_CLICK
import com.depromeet.team6.presentation.util.HomeAmplitude.REGISTER_MAP_MARKER_CLICKED
import com.depromeet.team6.presentation.util.amplitude.AmplitudeUtils
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val homeRepository: HomeRepository,
    private val getAddressFromCoordinatesUseCase: GetAddressFromCoordinatesUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getTaxiCostUseCase: GetTaxiCostUseCase,
    val getCourseSearchResultUseCase: GetCourseSearchResultsUseCase,
    private val getBusStartedUseCase: GetBusStartedUseCase,
    private val getBusArrivalUseCase: GetBusArrivalUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val refreshAlarmTimerUseCase: RefreshAlarmTimerUseCase,
    @ApplicationContext private val context: Context
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
                        departureTime = event.departureTime
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

    fun checkAfterRegisterDataComplete() {
        val currentState = currentState

        val isDataComplete = when {
            !currentState.isAlarmRegistered -> true
            currentState.isAlarmRegistered -> {
                currentState.departurePointName.isNotEmpty() &&
                    currentState.departureTime.isNotEmpty() &&
                    currentState.boardingTime.isNotEmpty() &&
                    currentState.homeArrivedTime.isNotEmpty() &&
                    currentState.firstTransportationName.isNotEmpty() &&
                    currentState.itineraryInfo != null
            }
            else -> false
        }

        setState {
            copy(
                afterRegisterDataLoadState = if (isDataComplete) LoadState.Success else LoadState.Loading
            )
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

    fun deleteAlarm(lastRouteId: String) {
        viewModelScope.launch {
            deleteAlarmUseCase(
                lastRouteId = lastRouteId
            )
                .onSuccess {
                    setEvent(HomeContract.HomeEvent.UpdateAlarmRegistered(false))
                    setEvent(HomeContract.HomeEvent.UpdateBusDeparted(false))

//                    stopPollingBusStarted()

                    homeRepository.clearAlarmData()

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
//                    if (it) {
//                        stopPollingBusStarted()
//                    }
                }.onFailure {
                    handleApiException(it) {
                        copy(
                            isBusDeparted = false
                        )
                    }
                    // TODO : 실패 5번 누적되면 푸시알림 구현
                    Timber.e("버스 출발여부 에러: ${it.message}")
//                    stopPollingBusStarted()
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

                    val isAllNotDefault = (
                        newMarkerPoint.lat != DEFAULT_MARKER_LAT &&
                            newMarkerPoint.lon != DEFAULT_MARKER_LON
                        ) &&
                        (
                            currentState.destinationPoint.lat != DEFAULT_DESTINATION_LAT &&
                                currentState.destinationPoint.lon != DEFAULT_DESTINATION_LON
                            )

                    if (isAllNotDefault) {
                        getTaxiCost()
                    }
                }
                .onFailure { exception ->
                    handleApiException(exception)
                }
        }
    }

//    fun startPollingBusStarted(routeId: String) {
//        busStartedPollingJob?.cancel()
//        lastRouteId = routeId
//
//        if (currentState.firtTransportTation == TransportType.BUS && currentState.isAlarmRegistered) {
//            busStartedPollingJob = viewModelScope.launch {
//                try {
//                    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
//                    val departureTime = LocalTime.parse(currentState.departureTime, timeFormatter)
//                    val now = LocalDateTime.now()
//
//                    var departureDateTimeToday = LocalDateTime.of(LocalDate.now(), departureTime)
//                    if (departureDateTimeToday.isBefore(now) || departureDateTimeToday.isEqual(now)) {
//                        departureDateTimeToday = departureDateTimeToday.plusDays(1)
//                    }
//
//                    val thirtyMinutesBefore = departureDateTimeToday.minusMinutes(30)
//
//                    if (now.isBefore(thirtyMinutesBefore)) {
//                        val delayUntilStart = java.time.Duration.between(now, thirtyMinutesBefore).toMillis()
//                        delay(delayUntilStart)
//                    }
//
//                    while (isActive) {
//                        Timber.d("버스 차고지 출발 여부 API 호출")
//                        loadDepartureTime()
//                        delay(60000)
//                    }
//                } catch (e: Exception) {
//                    Timber.e("startPollingBusStarted 오류: ${e.message}")
//                }
//            }
//        }
//    }
//
//    fun stopPollingBusStarted() {
//        busStartedPollingJob?.cancel()
//        busStartedPollingJob = null
//    }

    fun loadUserDepartureState() {
        viewModelScope.launch {
            val userDeparture = homeRepository.isUserDeparted()
            setEvent(HomeContract.HomeEvent.LoadUserDeparture(userDeparture))
            if (userDeparture && currentState.firtTransportTation == TransportType.BUS) {
                getBusArrival()
            }
        }
    }

    fun loadDepartureTime() {
        // 남은시간 3분 이하부터는 새로고침 불가
        val now: LocalDateTime = LocalDateTime.now()
        val departureTime = LocalDateTime.parse(currentState.departureTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val diff: Duration = Duration.between(now, departureTime)
        if (diff <= Duration.ofMinutes(3)) {
            return
        }

        viewModelScope.launch {
            refreshAlarmTimerUseCase()
                .onSuccess {
                    setState {
                        copy(
                            departureTime = it
                        )
                    }
                    AlarmScheduler.scheduleLockScreenAlarm(context, it)
                }
                .onFailure {
                    handleApiException(it)
                }
        }
    }

    fun loadAlarmAndCourseInfoFromPrefs() {
        viewModelScope.launch {
            setState { copy(alarmCheckLoadState = LoadState.Loading) }

            val isAlarmRegistered = homeRepository.isAlarmRegistered()
            val lastRouteId = homeRepository.getLastRouteId()

            setEvent(HomeContract.HomeEvent.UpdateAlarmRegistered(isAlarmRegistered))
            setEvent(lastRouteId.let { HomeContract.HomeEvent.UpdateLastRouteId(it) })

            if (!isAlarmRegistered) {
                setState {
                    copy(
                        alarmCheckLoadState = LoadState.Success,
                        afterRegisterDataLoadState = LoadState.Success
                    )
                }
            }

            setState {
                copy(
                    alarmCheckLoadState = LoadState.Success,
                    afterRegisterDataLoadState = LoadState.Loading
                )
            }

            homeRepository.getDeparturePoint()?.let { departurePoint ->
                setEvent(HomeContract.HomeEvent.UpdateDeparturePointName(departurePoint.name))
                setEvent(HomeContract.HomeEvent.UpdateDeparturePoint(departurePoint))
            }

            homeRepository.getLastCourseInfo()?.let { courseInfo ->
                setEvent(HomeContract.HomeEvent.LoadLegsResult(courseInfo))
                setEvent(HomeContract.HomeEvent.LoadDepartureDateTime(courseInfo.departureTime))
                setEvent(HomeContract.HomeEvent.LoadBoardingDateTime(courseInfo.boardingTime))
                setEvent(HomeContract.HomeEvent.LoadHomeArrivedTime(calculateArrivalTime(courseInfo.departureTime, courseInfo.totalTime)))
                setEvent(HomeContract.HomeEvent.LoadFirstTransportation(getFirstTransportation(courseInfo.legs)))
                setEvent(HomeContract.HomeEvent.LoadFirstTransportationNumber(getFirstTransportationNumber(courseInfo.legs)))
                setEvent(HomeContract.HomeEvent.LoadFirstTransportationName(getFirstTransportationName(courseInfo.legs)))

                if (getFirstTransportation(courseInfo.legs) == TransportType.BUS) {
//                    if (lastRouteId.isNotEmpty()) {
//                        startPollingBusStarted(lastRouteId)
//                    }
                } else if (getFirstTransportation(courseInfo.legs) == TransportType.SUBWAY) {
                    setEvent(HomeContract.HomeEvent.UpdateBusDeparted(true))
                }
            }

            checkAfterRegisterDataComplete()
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
                    subtypeIdx = 0,
                    passingStations = leg.passStopList
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

//    override fun onCleared() {
//        super.onCleared()
//        stopPollingBusStarted()
//    }

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
                }.onFailure { exception ->
                    handleApiException(exception) {
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

                val isAllNotDefault = (
                    currentState.markerPoint.lat != DEFAULT_MARKER_LAT &&
                        currentState.markerPoint.lon != DEFAULT_MARKER_LON
                    ) &&
                    (
                        userInfo.userHome.latitude != DEFAULT_DESTINATION_LAT &&
                            userInfo.userHome.longitude != DEFAULT_DESTINATION_LON
                        )

                if (isAllNotDefault) {
                    getTaxiCost()
                }
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
                lon = currentState.busArrivalParameter.lon,
                passingStations = currentState.busArrivalParameter.passingStations
            ).onSuccess { busArrival ->
                setState {
                    copy(
                        busRemainingStations = busArrival.realTimeBusArrival[0].remainingStations,
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
    }
}
