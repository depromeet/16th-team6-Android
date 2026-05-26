package com.depromeet.team6.presentation.ui.itinerary

import android.content.Context
import android.util.SparseArray
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.data.background.AlarmScheduler
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.RealTimeBusArrival
import com.depromeet.team6.domain.model.RouteLocation
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.domain.repository.HomeRepository
import com.depromeet.team6.domain.repository.UserInfoRepository
import com.depromeet.team6.domain.usecase.GetBusArrivalUseCase
import com.depromeet.team6.domain.usecase.GetTaxiCostUseCase
import com.depromeet.team6.domain.usecase.GetUserInfoUseCase
import com.depromeet.team6.domain.usecase.InitAlarmUseCase
import com.depromeet.team6.domain.usecase.PostAlarmUseCase
import com.depromeet.team6.presentation.util.AmplitudeCommon.SCREEN_NAME
import com.depromeet.team6.presentation.util.AmplitudeCommon.USER_ID
import com.depromeet.team6.presentation.util.ItineraryAmplitude.ITINERARY
import com.depromeet.team6.presentation.util.ItineraryAmplitude.ITINERARY_ALARM_REGISTER_BTN_CLICKED
import com.depromeet.team6.presentation.util.ItineraryAmplitude.ITINERARY_EVENT_ALARM_REGISTERED_SCREEN
import com.depromeet.team6.presentation.util.amplitude.AmplitudeUtils
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ItineraryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getBusArrivalUseCase: GetBusArrivalUseCase,
    private val postAlarmUseCase: PostAlarmUseCase,
    private val userInfoRepository: UserInfoRepository,
    private val homeRepository: HomeRepository,
    private val getTaxiCostUseCase: GetTaxiCostUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val initAlarmUseCase: InitAlarmUseCase
) : BaseViewModel<ItineraryContract.ItineraryUiState, ItineraryContract.ItinerarySideEffect, ItineraryContract.ItineraryEvent>() {
    override fun createInitialState(): ItineraryContract.ItineraryUiState = ItineraryContract.ItineraryUiState()

    init {
        AmplitudeUtils.trackEvent(
            "경로_상세_진입"
        )
        val isAlarmRegistered = homeRepository.isAlarmRegistered()
        val userDeparture = homeRepository.isUserDeparted()
        setState {
            copy(
                isAlarmRegistered = isAlarmRegistered,
                userDeparture = userDeparture
            )
        }
    }
    private var hasShownOverlayDialog = false

    override suspend fun handleEvent(event: ItineraryContract.ItineraryEvent) {
        when (event) {
            is ItineraryContract.ItineraryEvent.LoadLegsResult -> {
                setState {
                    copy(
                        itineraryInfo = event.result,
                        courseDataLoadState = LoadState.Success
                    )
                }
            }
            is ItineraryContract.ItineraryEvent.RefreshButtonClicked -> getRemainingBusArrivalTimes()

            is ItineraryContract.ItineraryEvent.RegisterAlarm -> {
                postAlarm(
                    departurePoint = currentState.departurePoint!!,
                    destinationPoint = currentState.destinationPoint!!,
                    alarmTimeStamp = currentState.itineraryInfo!!.departureTime,
                    lastRouteId = event.routeId
                )
                AmplitudeUtils.trackEventWithProperties(
                    eventName = ITINERARY_EVENT_ALARM_REGISTERED_SCREEN,
                    properties = mapOf(
                        SCREEN_NAME to ITINERARY,
                        USER_ID to userInfoRepository.getUserID(),
                        ITINERARY_ALARM_REGISTER_BTN_CLICKED to 1
                    )
                )
            }
            is ItineraryContract.ItineraryEvent.DismissOverlayPermissionDialog -> setState {
                copy(
                    showOverlayPermissionDialog = false
                )
            }
            is ItineraryContract.ItineraryEvent.DismissPermissionSnackbar -> setState {
                copy(
                    showPermissionSnackbar = false
                )
            }
            is ItineraryContract.ItineraryEvent.ShowOverlayPermissionDialog -> setState {
                copy(
                    showOverlayPermissionDialog = true
                )
            }
            is ItineraryContract.ItineraryEvent.ShowPermissionSnackbar -> setState {
                copy(
                    showPermissionSnackbar = true
                )
            }
        }
    }

    fun initItineraryInfo(courseInfoJSON: String, departurePointJSON: String, destinationPointJSON: String) {
        val courseInfo = Gson().fromJson(courseInfoJSON, CourseInfo::class.java)
        val departurePoint = Gson().fromJson(departurePointJSON, Address::class.java)
        val destinationPoint = Gson().fromJson(destinationPointJSON, Address::class.java)

        setState {
            copy(
                courseDataLoadState = LoadState.Success,
                departurePoint = departurePoint,
                destinationPoint = destinationPoint,
                itineraryInfo = courseInfo
            )
        }
        getRemainingBusArrivalTimes()
    }

    private fun postAlarm(departurePoint: Address, destinationPoint: Address, lastRouteId: String, alarmTimeStamp: String) {
        viewModelScope.launch {
            postAlarmUseCase(
                lastRouteId = lastRouteId
            )
                .onSuccess {
                    setSideEffect(ItineraryContract.ItinerarySideEffect.NavigateHomeWithToast)
                    getTaxiCost()
//                    saveAlarmData(departurePoint, destinationPoint, lastRouteId)
                    initAlarmUseCase(departurePoint, destinationPoint, currentState.itineraryInfo!!, lastRouteId)
                    AlarmScheduler.scheduleLockScreenAlarm(
                        context = context,
                        alarmTimeStamp = alarmTimeStamp
                    )
                    AlarmScheduler.scheduleAdditionalPushAlarm(context, alarmTimeStamp)
                    val courseSearchEnteredAt = homeRepository.getCourseSearchEnteredAt()
                    val alarmRegisterDurationSeconds = if (courseSearchEnteredAt > 0L) {
                        ((System.currentTimeMillis() - courseSearchEnteredAt) / 1000).coerceAtLeast(0)
                    } else {
                        0L
                    }
                    AmplitudeUtils.trackEventWithProperties(
                        eventName = "알람_등록",
                        properties = mapOf(
                            "알람_등록_시간" to alarmRegisterDurationSeconds
                        )
                    )
                }
                .onFailure { exception ->
                    handleApiException(exception)
                }
        }
    }

//    private fun saveAlarmData(departurePoint: Address, destinationPoint: Address, routeId: String) {
//        viewModelScope.launch {
//            try {
//                val registeredCourse = uiState.value.itineraryInfo
//
//                if (registeredCourse != null && departurePoint != null) {
//                    homeRepository.setDeparturePoint(departurePoint)
//                    homeRepository.setDestinationPoint(destinationPoint)
//                    homeRepository.setLastCourseInfo(registeredCourse)
//                    homeRepository.setLastRouteId(routeId)
//                    homeRepository.setAlarmRegistered(true)
//                }
//            } catch (e: Exception) {
//                Timber.e("알림 정보 spf 저장 오류: ${e.message}")
//            }
//        }
//    }

    private fun getTaxiCost() {
        viewModelScope.launch {
            getTaxiCostUseCase(
                routeLocation = RouteLocation(
                    startLat = uiState.value.departurePoint!!.lat,
                    startLon = uiState.value.departurePoint!!.lon,
                    endLat = uiState.value.destinationPoint!!.lat,
                    endLon = uiState.value.destinationPoint!!.lon
                )
            )
                .onSuccess {
                    getTaxiCostUseCase.saveTaxiCost(it)
                }.onFailure { exception ->
                    handleApiException(exception)
                }
        }
    }

//    fun postAdditionalAlarmSchedule(alarmTime: String) {
//        viewModelScope.launch {
//            getUserInfoUseCase()
//                .onSuccess {
//                    val freq = it.alertFrequencies
//                    AlarmScheduler.scheduleAdditionalPushAlarm(context, alarmTime)
//                }
//                .onFailure {
//                    handleApiException(it)
//                }
//        }
//    }

    private fun getRemainingBusArrivalTimes() {
        val newBusArrivalStatus = SparseArray<List<RealTimeBusArrival>>()

        viewModelScope.launch {
            val deferredList = currentState.itineraryInfo!!.legs.mapIndexedNotNull { idx, leg ->
                if (leg.transportType != TransportType.BUS) return@mapIndexedNotNull null

                async {
                    val result = getBusArrivalUseCase(
                        routeName = leg.routeName!!,
                        stationName = leg.startPoint.name,
                        lat = leg.startPoint.lat,
                        lon = leg.startPoint.lon,
                        passingStations = leg.passStopList
                    ).onFailure { exception ->
                        handleApiException(exception = exception)
                    }
                    result.mapCatching {
                        Timber.d("busArrivalStatusAPIResult : $it")
                        idx to it.realTimeBusArrival.take(2)
                    }.getOrNull()
                }
            }
            // 모든 API 호출이 끝난 뒤 결과를 처리
            val results = deferredList.awaitAll().filterNotNull()
            for ((idx, arrival) in results) {
                newBusArrivalStatus.put(idx, arrival)
            }
            setState {
                copy(
                    busArrivalStatus = newBusArrivalStatus
                )
            }
        }
        Timber.d("busArrivalStatus ViewModel : ${currentState.busArrivalStatus}")
    }

    fun showOverlayPermissionDialog() {
        setEvent(ItineraryContract.ItineraryEvent.ShowOverlayPermissionDialog)
    }

    fun dismissOverlayPermissionDialog() {
        setEvent(ItineraryContract.ItineraryEvent.DismissOverlayPermissionDialog)
    }

    fun shouldShowOverlayDialog(): Boolean {
        return !hasShownOverlayDialog
    }

    fun markOverlayDialogAsShow() {
        hasShownOverlayDialog = true
    }

    fun hasShownOverlayDialogBefore(): Boolean {
        return hasShownOverlayDialog
    }

    fun showPermissionSnackbar() {
        setEvent(ItineraryContract.ItineraryEvent.ShowPermissionSnackbar)
    }

    fun dismissPermissionSnackbar() {
        setEvent(ItineraryContract.ItineraryEvent.DismissPermissionSnackbar)
    }
}
