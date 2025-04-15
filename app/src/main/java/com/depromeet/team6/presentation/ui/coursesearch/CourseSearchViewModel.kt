package com.depromeet.team6.presentation.ui.coursesearch

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.repository.UserInfoRepository
import com.depromeet.team6.domain.usecase.GetCourseSearchResultsUseCase
import com.depromeet.team6.domain.usecase.PostAlarmUseCase
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
    private val loadSearchResult: GetCourseSearchResultsUseCase,
    private val postAlarmUseCase: PostAlarmUseCase,
    private val userInfoRepository: UserInfoRepository,
    @ApplicationContext private val context: Context
) : BaseViewModel<CourseSearchContract.CourseUiState, CourseSearchContract.CourseSideEffect, CourseSearchContract.CourseEvent>() {
    override fun createInitialState(): CourseSearchContract.CourseUiState = CourseSearchContract.CourseUiState()

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    override suspend fun handleEvent(event: CourseSearchContract.CourseEvent) {
        when (event) {
            is CourseSearchContract.CourseEvent.RegisterAlarm -> CourseSearchContract.CourseSideEffect.ShowNotificationToast
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
                .onFailure {
                    setState {
                        copy(
                            courseDataLoadState = LoadState.Error
                        )
                    }
                    setSideEffect(CourseSearchContract.CourseSideEffect.ShowSearchFailedToast(it.message!!))
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
                setState { copy(courseDataLoadState = LoadState.Error) }
            }
        } catch (e: Exception) {
            setState { copy(courseDataLoadState = LoadState.Error) }
        }
    }

    fun registerAlarm() {
        viewModelScope.launch {
            setEvent(CourseSearchContract.CourseEvent.RegisterAlarm)
        }
    }

    fun postAlarm(lastRouteId: String) {
        viewModelScope.launch {
            if (postAlarmUseCase(
                    lastRouteId = lastRouteId
                ).isSuccessful
            ) {
                setEvent(CourseSearchContract.CourseEvent.RegisterAlarm)
                setSideEffect(CourseSearchContract.CourseSideEffect.NavigateHomeWithToast)
            } else {
                setEvent(CourseSearchContract.CourseEvent.RegisterAlarm)
            }
        }
    }
}
