package com.depromeet.team6.presentation.ui.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.R
import com.depromeet.team6.data.background.AlarmScheduler
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.RouteLocation
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.domain.repository.HomeRepository
import com.depromeet.team6.domain.repository.UserInfoRepository
import com.depromeet.team6.domain.usecase.DeleteAlarmUseCase
import com.depromeet.team6.domain.usecase.GetAddressFromCoordinatesUseCase
import com.depromeet.team6.domain.usecase.GetAppVersionUseCase
import com.depromeet.team6.domain.usecase.GetBusArrivalUseCase
import com.depromeet.team6.domain.usecase.GetBusStartedUseCase
import com.depromeet.team6.domain.usecase.GetCourseSearchResultsUseCase
import com.depromeet.team6.domain.usecase.GetIsServiceRegionUseCase
import com.depromeet.team6.domain.usecase.GetRealtimeLocationUseCase
import com.depromeet.team6.domain.usecase.GetTaxiCostUseCase
import com.depromeet.team6.domain.usecase.GetUserInfoUseCase
import com.depromeet.team6.domain.usecase.RefreshAlarmTimerUseCase
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.depromeet.team6.presentation.model.home.MapFocusState
import com.depromeet.team6.presentation.util.AmplitudeCommon.SCREEN_NAME
import com.depromeet.team6.presentation.util.AmplitudeCommon.USER_ID
import com.depromeet.team6.presentation.util.DefaultMarkerDestination.DEFAULT_DESTINATION_LAT
import com.depromeet.team6.presentation.util.DefaultMarkerDestination.DEFAULT_DESTINATION_LON
import com.depromeet.team6.presentation.util.DefaultMarkerDestination.DEFAULT_MARKER_LAT
import com.depromeet.team6.presentation.util.DefaultMarkerDestination.DEFAULT_MARKER_LON
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_EVENT_CHARACTER_CLICK_AFTER_ALARM
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_EVENT_CHARACTER_CLICK_BEFORE_ALARM
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_EVENT_ITINERARY_BTN_CLICK
import com.depromeet.team6.presentation.util.HomeAmplitude.HOME_EVENT_REGISTER_MAP_MARKER_CLICK
import com.depromeet.team6.presentation.util.HomeAmplitude.REGISTER_MAP_MARKER_CLICKED
import com.depromeet.team6.presentation.util.amplitude.AmplitudeUtils
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import timber.log.Timber
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
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
    private val getRealtimeLocationUseCase: GetRealtimeLocationUseCase,
    private val getAppVersionUseCase: GetAppVersionUseCase,
    private val getIsServiceRegionUseCase: GetIsServiceRegionUseCase,
    @ApplicationContext private val context: Context
) : BaseViewModel<HomeContract.HomeUiState, HomeContract.HomeSideEffect, HomeContract.HomeEvent>() {
    private var lastRouteId: String = ""
    private var taxiCostJob: Job? = null
    private var characterTaxiCostDebounceJob: Job? = null
    private var lastTaxiCostRoute: RouteLocation? = null

    private val beforeDepartMessages = listOf(
        "막차 놓치면 택시비 약 34,000원",
        "시간에 맞춰 알림을 드릴게요",
        "교통 상황에 따라 시간이 달라질 수 있어요"
    )

    private val afterDepartMessages = listOf(
        "교통 상황에 따라 시간이 달라질 수 있어요",
        "믿을 수 있는 공공 데이터를 활용하고 있어요",
        "막차 놓치면 택시비 약 34,000원"
    )

    private var messageIdx = 0

    init {
        checkAppVersion()
        viewModelScope.launch {
            loadAlarmAndCourseInfoFromPrefs()
            val initialSpeech = if (loadUserDepartureState()) {
                HomeContract.SpeechRequest(
                    listOf(
                        "교통 상황에 따라 시간이 달라질 수 있어요"
                    )
                )
            } else {
                val taxiCost = getTaxiCostUseCase.getLastSavedTaxiCost()
                val formattedCost = String.format(Locale.KOREA, "%,d", taxiCost)
                HomeContract.SpeechRequest(
                    messages = listOf(
                        context.getString(R.string.home_bubble_taxi_cost_message, formattedCost)
                    )
                )
            }
            val focusState = if (homeRepository.isAlarmRegistered()) {
                MapFocusState.Departure
            } else {
                MapFocusState.Current
            }

            setState {
                copy(
                    loadState = LoadState.Success,
                    characterMessages = initialSpeech,
                    isMapFocused = focusState
                )
            }
        }
    }

    override fun createInitialState(): HomeContract.HomeUiState = HomeContract.HomeUiState()

    override suspend fun handleEvent(event: HomeContract.HomeEvent) {
        when (event) {
            is HomeContract.HomeEvent.UpdateAlarmRegistered ->
                setState {
                    copy(
                        isAlarmRegistered = event.isRegistered,
                        isMapFocused = if (event.isRegistered) {
                            MapFocusState.Departure
                        } else {
                            isMapFocused
                        }
                    )
                }
            is HomeContract.HomeEvent.UpdateBusDeparted -> setState { copy(isBusDeparted = event.isBusDeparted) }
            is HomeContract.HomeEvent.UpdateSpeechBubbleVisibility -> setState {
                copy(
                    showSpeechBubble = event.show
                )
            }

            is HomeContract.HomeEvent.OnCharacterClick -> {
                if (currentState.isAlarmRegistered) {
                    requestSpeechAfterRegisterAlarm()
                    AmplitudeUtils.trackEventWithProperties(
                        eventName = HOME_EVENT_CHARACTER_CLICK_AFTER_ALARM,
                        properties = mapOf(
                            HOME_EVENT_CHARACTER_CLICK_AFTER_ALARM to 1
                        )
                    )
                } else {
                    requestTaxiCostFromCharacterClick()
                    AmplitudeUtils.trackEventWithProperties(
                        eventName = HOME_EVENT_CHARACTER_CLICK_BEFORE_ALARM,
                        properties = mapOf(
                            HOME_EVENT_CHARACTER_CLICK_BEFORE_ALARM to 1
                        )
                    )
                }
            }

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

            is HomeContract.HomeEvent.RequestCharacterSpeech -> {
                setState {
                    copy(
                        characterMessages = HomeContract.SpeechRequest(
                            messages = event.messages
                        )
                    )
                }
            }

            is HomeContract.HomeEvent.OnSearchClick -> {
                val distance = calculateDistance(
                    lat1 = currentState.markerPoint.lat,
                    lon1 = currentState.markerPoint.lon,
                    lat2 = currentState.destinationPoint.lat,
                    lon2 = currentState.destinationPoint.lon
                )
                if (distance <= 800.0) {
                    setSideEffect(HomeContract.HomeSideEffect.ShowTooCloseDialog)
                } else {
                    getIsServiceRegionUseCase(
                        lat = currentState.markerPoint.lat,
                        lon = currentState.markerPoint.lon
                    ).onSuccess { isServiceRegion ->
                        if (isServiceRegion) {
                            setSideEffect(HomeContract.HomeSideEffect.NavigateToCourseSearch)
                        } else {
                            setSideEffect(HomeContract.HomeSideEffect.ShowOutOfServiceRegionBottomSheet)
                        }
                    }.onFailure {
                        setSideEffect(HomeContract.HomeSideEffect.ShowOutOfServiceRegionBottomSheet)
                    }
                }
            }
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
                    AlarmScheduler.unScheduleAllAlarms(context)
//                    getUserInfoUseCase()
//                        .onSuccess {
//                            val freq = it.alertFrequencies
//                            AlarmScheduler.unScheduleAllAlarms(context)
//                        }
//                        .onFailure {
//                            handleApiException(it)
//                            Firebase.crashlytics.recordException(RuntimeException("deleteAlarm 오류 : 알람취소를 눌렀지만 실제로 unschedule 로직이 실행되지 않음"))
//                        }
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
                    handleApiException(it) { code ->
                        currentState.copy(
                            isBusDeparted = false
                        )
                    }
//                    stopPollingBusStarted()
                }
        }
    }

    fun getCenterLocationAddress(location: LatLng) {
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
                    if (!currentState.userDeparture) {
                        requestTaxiCost(newMarkerPoint)
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

    fun loadUserDepartureState(): Boolean {
        val userDeparture = homeRepository.isUserDeparted()
        setState {
            copy(
                userDeparture = userDeparture
            )
        }
        if (userDeparture && currentState.firtTransportTation == TransportType.BUS) {
            getBusArrival()
        }

        return userDeparture
    }

    fun loadDepartureTime() {
        // 사용자가 이미 출발한 상태라면 알람을 재설정하지 않는다.
        // 이 가드가 없으면 HomeScreen onResume 시 loadDepartureTime()이 호출되어
        // 출발 후에도 알람이 재등록되는 버그가 발생한다.
        if (homeRepository.isUserDeparted()) {
            Timber.d("loadDepartureTime: 사용자가 이미 출발했으므로 알람 재설정 건너뜀")
            return
        }

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
                    homeRepository.getLastCourseInfo()?.let { courseInfo ->
                        homeRepository.setLastCourseInfo(
                            courseInfo.copy(departureTime = it)
                        )
                    }
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
                setEvent(
                    HomeContract.HomeEvent.LoadHomeArrivedTime(
                        calculateArrivalTime(
                            courseInfo.departureTime,
                            courseInfo.totalTime
                        )
                    )
                )
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
                    routeName = leg.routeName.orEmpty(),
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
                    firstTransportationName = leg.routeName
                        ?.substringAfter(":", missingDelimiterValue = leg.routeName.orEmpty())
                        ?.trim()
                        .orEmpty()
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

    private fun requestTaxiCost(markerPoint: Address, force: Boolean = false) {
        val routeLocation = RouteLocation(
            startLat = markerPoint.lat,
            startLon = markerPoint.lon,
            endLat = currentState.destinationPoint.lat,
            endLon = currentState.destinationPoint.lon
        )

        if (!force && shouldSkipTaxiCostRequest(routeLocation)) {
            return
        }

        taxiCostJob?.cancel()
        taxiCostJob = viewModelScope.launch {
            delay(TAXI_COST_REQUEST_DEBOUNCE_MS)
            getTaxiCost(routeLocation)
        }
    }

    private fun requestTaxiCostFromCharacterClick() {
        viewModelScope.launch {
            val savedTaxiCost = getTaxiCostUseCase.getLastSavedTaxiCost()
            if (savedTaxiCost > 0) {
                showTaxiCostSpeech(savedTaxiCost)
            }
        }

        characterTaxiCostDebounceJob?.cancel()
        characterTaxiCostDebounceJob = viewModelScope.launch {
            delay(CHARACTER_TAXI_COST_DEBOUNCE_MS)

            val routeLocation = RouteLocation(
                startLat = currentState.markerPoint.lat,
                startLon = currentState.markerPoint.lon,
                endLat = currentState.destinationPoint.lat,
                endLon = currentState.destinationPoint.lon
            )

            if (!isTaxiCostRouteValid(routeLocation)) {
                return@launch
            }

            getTaxiCost(routeLocation)
        }
    }

    private fun getTaxiCost(routeLocation: RouteLocation) {
        viewModelScope.launch {
            getTaxiCostUseCase(
                routeLocation = routeLocation
            )
                .onSuccess {
                    lastTaxiCostRoute = routeLocation
                    setState {
                        copy(
                            taxiCost = it,
                        )
                    }
                    showTaxiCostSpeech(it)
                    getTaxiCostUseCase.saveTaxiCost(it)
                }.onFailure { exception ->
                    handleApiException(exception) {
                        currentState.copy(
                            taxiCost = 0
                        )
                    }
                }
        }
    }

    private fun showTaxiCostSpeech(taxiCost: Int) {
        val formattedCost = String.format(Locale.KOREA, "%,d", taxiCost)
        val resultString = context.getString(R.string.home_bubble_taxi_cost_message, formattedCost)
        setState {
            copy(
                characterMessages = HomeContract.SpeechRequest(
                    messages = listOf(resultString)
                )
            )
        }
    }

    private fun requestSpeechAfterRegisterAlarm() {
        val message = if (currentState.userDeparture) {
            afterDepartMessages[messageIdx]
        } else {
            beforeDepartMessages[messageIdx]
        }
        setState {
            copy(
                characterMessages = HomeContract.SpeechRequest(
                    messages = listOf(message)
                )
            )
        }
        messageIdx = if (currentState.userDeparture) {
            (messageIdx + 1) % afterDepartMessages.size
        } else {
            (messageIdx + 1) % beforeDepartMessages.size
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
                    requestTaxiCost(currentState.markerPoint, force = true)
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

    private fun checkAppVersion() {
        val installedVersion = getCurrentVersionName()?.cleanVersion() ?: "0.0.0"
        viewModelScope.launch {
            getAppVersionUseCase().onSuccess { appVersion ->
                val latestVersion = appVersion.removePrefix("Success(")
                    .removePrefix("v")
                    .removeSuffix(")")
                    .cleanVersion()

                Timber.d("latestVersion: $latestVersion, installedVersion: $installedVersion")

                when (compareVersions(installedVersion, latestVersion)) {
                    VersionResult.UPDATE_REQUIRED -> {
                        setSideEffect(HomeContract.HomeSideEffect.ShowUpdateRequiredDialog)
                    }

                    VersionResult.UPDATE_OPTIONAL -> {
                        setSideEffect(HomeContract.HomeSideEffect.ShowUpdateOptionalDialog)
                    }

                    VersionResult.UP_TO_DATE -> {
                        Timber.d("최신 버전입니다")
                    }
                }
            }.onFailure {
                Timber.e(it, "앱 버전 확인 실패")
            }
        }
    }

    private fun String.cleanVersion(): String =
        this.replace("[^0-9.]".toRegex(), "")

    private fun compareVersions(installed: String, latest: String): VersionResult {
        val installedParts = installed.split(".").map { it.toIntOrNull() ?: 0 }
        val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }

        val (a1, b1, c1) = installedParts + List(3 - installedParts.size) { 0 }
        val (a2, b2, c2) = latestParts + List(3 - latestParts.size) { 0 }

        return when {
            a1 != a2 -> if (a1 < a2) VersionResult.UPDATE_REQUIRED else VersionResult.UP_TO_DATE
            b1 != b2 -> if (b1 < b2) VersionResult.UPDATE_REQUIRED else VersionResult.UP_TO_DATE
            c1 != c2 -> if (c1 < c2) VersionResult.UPDATE_OPTIONAL else VersionResult.UP_TO_DATE
            else -> VersionResult.UP_TO_DATE
        }
    }

    private enum class VersionResult {
        UPDATE_REQUIRED,
        UPDATE_OPTIONAL,
        UP_TO_DATE
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val φ1 = Math.toRadians(lat1)
        val φ2 = Math.toRadians(lat2)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(φ1) * Math.cos(φ2) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
        return 2 * r * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    }

    private fun shouldSkipTaxiCostRequest(routeLocation: RouteLocation): Boolean {
        if (!isTaxiCostRouteValid(routeLocation)) {
            return true
        }

        val previousRoute = lastTaxiCostRoute ?: return false
        val startDistance = calculateDistance(
            lat1 = previousRoute.startLat,
            lon1 = previousRoute.startLon,
            lat2 = routeLocation.startLat,
            lon2 = routeLocation.startLon
        )
        val endDistance = calculateDistance(
            lat1 = previousRoute.endLat,
            lon1 = previousRoute.endLon,
            lat2 = routeLocation.endLat,
            lon2 = routeLocation.endLon
        )

        return startDistance < TAXI_COST_REQUEST_MIN_DISTANCE_METER &&
            endDistance < TAXI_COST_REQUEST_MIN_DISTANCE_METER
    }

    private fun isTaxiCostRouteValid(routeLocation: RouteLocation): Boolean {
        if (
            routeLocation.startLat == DEFAULT_MARKER_LAT &&
            routeLocation.startLon == DEFAULT_MARKER_LON
        ) {
            return false
        }

        if (
            routeLocation.endLat == DEFAULT_DESTINATION_LAT &&
            routeLocation.endLon == DEFAULT_DESTINATION_LON
        ) {
            return false
        }

        return true
    }

    private fun getCurrentVersionName(): String? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            Timber.e(e, "Failed to get app version name")
            "0.0.0"
        }
    }

    companion object {
        private const val MY_PREFERENCES_NAME = "MyPreferences"
        private const val TAXI_COST_REQUEST_DEBOUNCE_MS = 400L
        private const val TAXI_COST_REQUEST_MIN_DISTANCE_METER = 100.0
        private const val CHARACTER_TAXI_COST_DEBOUNCE_MS = 1_000L
    }
}
