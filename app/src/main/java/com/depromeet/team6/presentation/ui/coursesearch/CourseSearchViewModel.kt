package com.depromeet.team6.presentation.ui.coursesearch

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.data.background.AlarmScheduler
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.RouteLocation
import com.depromeet.team6.domain.repository.HomeRepository
import com.depromeet.team6.domain.repository.UserInfoRepository
import com.depromeet.team6.domain.usecase.DeleteAlarmUseCase
import com.depromeet.team6.domain.usecase.GetCourseSearchResultsUseCase
import com.depromeet.team6.domain.usecase.GetTaxiCostUseCase
import com.depromeet.team6.domain.usecase.GetUserInfoUseCase
import com.depromeet.team6.domain.usecase.PostAlarmUseCase
import com.depromeet.team6.presentation.ui.coursesearch.navigation.CourseSearchRoute.DEPARTURE_POINT
import com.depromeet.team6.presentation.ui.coursesearch.navigation.CourseSearchRoute.DESTINATION_POINT
import com.depromeet.team6.presentation.util.AmplitudeCommon.SCREEN_NAME
import com.depromeet.team6.presentation.util.AmplitudeCommon.USER_ID
import com.depromeet.team6.presentation.util.CourseSearchAmplitude.COURSE_SEARCH
import com.depromeet.team6.presentation.util.CourseSearchAmplitude.COURSE_SEARCH_ALARM_REGISTERED
import com.depromeet.team6.presentation.util.CourseSearchAmplitude.COURSE_SEARCH_EVENT_ALARM_REGISTERED
import com.depromeet.team6.presentation.util.CourseSearchAmplitude.COURSE_SEARCH_EVENT_CARD_CLICKED
import com.depromeet.team6.presentation.util.CourseSearchAmplitude.COURSE_SEARCH_EVENT_DURATION
import com.depromeet.team6.presentation.util.CourseSearchAmplitude.COURSE_SEARCH_EVENT_ITEM_TOGGLED
import com.depromeet.team6.presentation.util.CourseSearchAmplitude.COURSE_SEARCH_ITEM_CARD_CLICKED
import com.depromeet.team6.presentation.util.CourseSearchAmplitude.COURSE_SEARCH_ITEM_DETAIL_TEXT_CLICKED
import com.depromeet.team6.presentation.util.CourseSearchAmplitude.COURSE_SEARCH_STAY_TIME
import com.depromeet.team6.presentation.util.CourseSearchAmplitude.COURSE_SEARCH_TOGGLE_DISABLED
import com.depromeet.team6.presentation.util.amplitude.AmplitudeUtils
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CourseSearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadSearchResult: GetCourseSearchResultsUseCase,
    private val postAlarmUseCase: PostAlarmUseCase,
    private val userInfoRepository: UserInfoRepository,
    private val homeRepository: HomeRepository,
    private val getTaxiCostUseCase: GetTaxiCostUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    @ApplicationContext private val context: Context
) : BaseViewModel<CourseSearchContract.CourseUiState, CourseSearchContract.CourseSideEffect, CourseSearchContract.CourseEvent>() {

    init {
        val departurePointJSON: String = savedStateHandle[DEPARTURE_POINT] ?: "알 수 없음"
        val destinationPointJSON: String = savedStateHandle[DESTINATION_POINT] ?: "알 수 없음"
        setEvent(CourseSearchContract.CourseEvent.InitUiState(departurePointJSON, destinationPointJSON))
    }
    private var enterTime: Long = 0

    override fun createInitialState(): CourseSearchContract.CourseUiState = CourseSearchContract.CourseUiState()

    override suspend fun handleEvent(event: CourseSearchContract.CourseEvent) {
        when (event) {
            is CourseSearchContract.CourseEvent.OnEnter -> {
                enterTime = System.currentTimeMillis()
            }
            is CourseSearchContract.CourseEvent.OnExit -> {
                val stayTime = (System.currentTimeMillis() - enterTime) / 1000
                AmplitudeUtils.trackEventWithProperties(
                    eventName = COURSE_SEARCH_EVENT_DURATION,
                    properties = mapOf(
                        SCREEN_NAME to COURSE_SEARCH,
                        USER_ID to userInfoRepository.getUserID(),
                        COURSE_SEARCH_STAY_TIME to stayTime
                    )
                )
            }
            is CourseSearchContract.CourseEvent.RegisterAlarm -> {
                setSideEffect(CourseSearchContract.CourseSideEffect.ShowNotificationToast)
                AmplitudeUtils.trackEventWithProperties(
                    eventName = COURSE_SEARCH_EVENT_ALARM_REGISTERED,
                    properties = mapOf(
                        SCREEN_NAME to COURSE_SEARCH,
                        USER_ID to userInfoRepository.getUserID(),
                        COURSE_SEARCH_ALARM_REGISTERED to 1
                    )
                )
            }
            is CourseSearchContract.CourseEvent.LoadCourseSearchResult -> setState {
                copy(
                    courseDataLoadState = LoadState.Success,
                    courseData = event.searchResult
                )
            }
            is CourseSearchContract.CourseEvent.InitiateDepartureDestinationPoint -> {
                setState {
                    copy(
                        startingPoint = event.departurePoint,
                        destinationPoint = event.destinationPoint
                    )
                }
                getSearchResults()
            }

            is CourseSearchContract.CourseEvent.InitUiState -> setDepartureDestination(event.departure, event.destination)
            is CourseSearchContract.CourseEvent.ItemCourseDetailToggleClick -> {
                AmplitudeUtils.trackEventWithProperties(
                    eventName = COURSE_SEARCH_EVENT_ITEM_TOGGLED,
                    properties = mapOf(
                        SCREEN_NAME to COURSE_SEARCH,
                        USER_ID to userInfoRepository.getUserID(),
                        COURSE_SEARCH_TOGGLE_DISABLED to 1
                    )
                )
            }
            is CourseSearchContract.CourseEvent.ItemCardClick -> {
                when (event.isTextClicked) {
                    true -> AmplitudeUtils.trackEventWithProperties(
                        eventName = COURSE_SEARCH_EVENT_CARD_CLICKED,
                        properties = mapOf(
                            SCREEN_NAME to COURSE_SEARCH,
                            USER_ID to userInfoRepository.getUserID(),
                            COURSE_SEARCH_ITEM_DETAIL_TEXT_CLICKED to 1
                        )
                    )
                    false -> AmplitudeUtils.trackEventWithProperties(
                        eventName = COURSE_SEARCH_EVENT_CARD_CLICKED,
                        properties = mapOf(
                            SCREEN_NAME to COURSE_SEARCH,
                            USER_ID to userInfoRepository.getUserID(),
                            COURSE_SEARCH_ITEM_CARD_CLICKED to 1
                        )
                    )
                }
            }
            is CourseSearchContract.CourseEvent.ShowDeleteAlarmDialog -> setState {
                copy(
                    showDeleteAlarmDialog = true,
                    selectedRouteId = event.routeId
                )
            }
            is CourseSearchContract.CourseEvent.DismissDeleteAlarmDialog -> setState {
                copy(
                    showDeleteAlarmDialog = false,
                    selectedRouteId = ""
                )
            }
        }
    }

    fun getUserId(): Int {
        return userInfoRepository.getUserID()
    }

    fun setSortType(sortType: Int) {
        setState { copy(sortType = sortType) }
        if (uiState.value.startingPoint != null && uiState.value.destinationPoint != null) {
            getSearchResults()
        }
    }

    private fun getSearchResults() {
        setState {
            copy(
                courseDataLoadState = LoadState.Loading
            )
        }
        viewModelScope.launch {
            loadSearchResult(
                startPoint = uiState.value.startingPoint!!,
                endPoint = uiState.value.destinationPoint!!,
                sortType = uiState.value.sortType
            )
                .onSuccess {
                    setEvent(CourseSearchContract.CourseEvent.LoadCourseSearchResult(it))
                }
                .onFailure { exception ->
                    handleApiException(exception)
                }
        }
    }

    fun showDeleteAlarmDialog(routeId: String) {
        setEvent(CourseSearchContract.CourseEvent.ShowDeleteAlarmDialog(routeId))
    }

    fun dismissDeleteAlarmDialog() {
        setEvent(CourseSearchContract.CourseEvent.DismissDeleteAlarmDialog)
    }

    fun setDepartureDestination(departure: String, destination: String) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        try {
            val fromLockScreen = sharedPreferences.getBoolean("fromLockScreen", false)

            if (fromLockScreen) {
                setState { copy(sortType = 2) }

                sharedPreferences.edit().remove("fromLockScreen").apply()
            }

            val departurePoint = Gson().fromJson(departure, Address::class.java)
            val destinationPoint = Gson().fromJson(destination, Address::class.java)

            if (departurePoint != null && destinationPoint != null) {
                setEvent(CourseSearchContract.CourseEvent.InitiateDepartureDestinationPoint(departurePoint, destinationPoint))
            } else {
                setState {
                    copy(
                        startingPoint = departurePoint,
                        destinationPoint = destinationPoint
                    )
                }
                getSearchResults()
            }
        } catch (e: Exception) {
            setState { copy(courseDataLoadState = LoadState.Error) }
        }
    }

    private fun getTaxiCost() {
        viewModelScope.launch {
            getTaxiCostUseCase(
                routeLocation = RouteLocation(
                    startLat = uiState.value.startingPoint!!.lat,
                    startLon = uiState.value.startingPoint!!.lon,
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

    private fun saveAlarmData(departurePoint: String, destinationPoint: String, routeId: String) {
        viewModelScope.launch {
            try {
                val departureAddress = Gson().fromJson(departurePoint, Address::class.java)
                val registeredCourse = uiState.value.courseData.find { it.routeId == routeId }

                if (registeredCourse != null && departureAddress != null) {
                    homeRepository.setDeparturePoint(departureAddress)
                    homeRepository.setDestinationPoint(destinationPoint)
                    homeRepository.setLastCourseInfo(registeredCourse)
                    homeRepository.setLastRouteId(routeId)
                    homeRepository.setAlarmRegistered(true)
                }
            } catch (e: Exception) {
                Timber.e("알림 정보 spf 저장 오류: ${e.message}")
            }
        }
    }

    fun postAlarm(departurePoint: String, destinationPoint: String, lastRouteId: String, alarmTimeStamp: String) {
        viewModelScope.launch {
            postAlarmUseCase(
                lastRouteId = lastRouteId
            )
                .onSuccess {
                    setEvent(CourseSearchContract.CourseEvent.RegisterAlarm)
                    setSideEffect(CourseSearchContract.CourseSideEffect.NavigateHomeWithToast)
                    getTaxiCost()
                    saveAlarmData(departurePoint, destinationPoint, lastRouteId)
                    AlarmScheduler.scheduleLockScreenAlarm(
                        context = context,
                        timeStamp = alarmTimeStamp
                    )
                    postAdditionalAlarmSchedule(alarmTimeStamp)
                }
                .onFailure { exception ->
                    handleApiException(exception)
                }
        }
    }

    fun postAdditionalAlarmSchedule(alarmTime: String) {
        viewModelScope.launch {
            getUserInfoUseCase()
                .onSuccess {
                    val freq = it.alertFrequencies
                    AlarmScheduler.scheduleAdditionalPushAlarm(context, alarmTime, freq)
                }
                .onFailure {
                    handleApiException(it)
                }
        }
    }

    fun updateAlarm(departurePoint: String, destinationPoint: String, lastRouteId: String, alarmTimeStamp: String) {
        viewModelScope.launch {
            deleteAlarmUseCase(
                lastRouteId = lastRouteId
            )
                .onSuccess {
                    postAlarm(departurePoint, destinationPoint, lastRouteId, alarmTimeStamp)
                }
                .onFailure { exception ->
                    handleApiException(exception)
                }
        }
    }
}
