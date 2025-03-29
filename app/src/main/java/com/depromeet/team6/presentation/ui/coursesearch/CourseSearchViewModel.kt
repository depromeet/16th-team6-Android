package com.depromeet.team6.presentation.ui.coursesearch

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.usecase.GetCourseSearchResultsUseCase
import com.depromeet.team6.domain.usecase.PostAlarmUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseSearchViewModel @Inject constructor(
    private val loadSearchResult: GetCourseSearchResultsUseCase,
    private val postAlarmUseCase: PostAlarmUseCase
) : BaseViewModel<CourseSearchContract.CourseUiState, CourseSearchContract.CourseSideEffect, CourseSearchContract.CourseEvent>() {
    override fun createInitialState(): CourseSearchContract.CourseUiState = CourseSearchContract.CourseUiState()

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
                endPoint = uiState.value.destinationPoint!!
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

    fun setDepartureDestination(departure: String, destination: String) {
        val departurePoint = Gson().fromJson(departure, Address::class.java)
        val destinationPoint = Gson().fromJson(destination, Address::class.java)

        setEvent(CourseSearchContract.CourseEvent.InitiateDepartureDestinationPoint(departurePoint, destinationPoint))
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
