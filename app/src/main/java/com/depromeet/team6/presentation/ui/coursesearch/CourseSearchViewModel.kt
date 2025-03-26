package com.depromeet.team6.presentation.ui.coursesearch

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.model.course.WayPoint
import com.depromeet.team6.domain.usecase.GetCourseSearchResultsUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CourseSearchViewModel @Inject constructor(
    private val loadSearchResult: GetCourseSearchResultsUseCase
) : BaseViewModel<CourseSearchContract.CourseUiState, CourseSearchContract.CourseSideEffect, CourseSearchContract.CourseEvent>() {
    override fun createInitialState(): CourseSearchContract.CourseUiState = CourseSearchContract.CourseUiState()

    override suspend fun handleEvent(event: CourseSearchContract.CourseEvent) {
        when (event) {
            is CourseSearchContract.CourseEvent.RegisterAlarm -> CourseSearchContract.CourseSideEffect.ShowNotificationToast
            is CourseSearchContract.CourseEvent.LoadCourseSearchResult -> setState {
                copy(
                    courseData = event.searchResult
                )
            }
            is CourseSearchContract.CourseEvent.InitiateDepartureDestinationPoint -> {
                setState {
                    copy(
                        courseDataLoadState = LoadState.Success,
                        startingPoint = event.departurePoint,
                        destinationPoint = event.destinationPoint
                    )
                }
                getSearchResults()
            }
        }
    }

    private fun getSearchResults(){
        viewModelScope.launch {
            loadSearchResult(
                startPoint = uiState.value.startingPoint!!,
                endPoint = uiState.value.destinationPoint!!
            )
                .onSuccess {
                    Timber.tag("alksdjhflakhjfdlhkjsdflhjk").d(it.toString())
                    setEvent(CourseSearchContract.CourseEvent.LoadCourseSearchResult(it))
                }
                .onFailure {
                    Timber.tag("alksdjhflakhjfdlhkjsdflhjk").d(it.toString())
                }
        }
    }

    fun setDepartureDestination(departure: String, destination: String) {
        val departurePoint = Gson().fromJson(departure, WayPoint::class.java)
        val destinationPoint = Gson().fromJson(destination, WayPoint::class.java)

        setEvent(CourseSearchContract.CourseEvent.InitiateDepartureDestinationPoint(departurePoint, destinationPoint))
    }

    fun registerAlarm() {
        viewModelScope.launch {
            setEvent(CourseSearchContract.CourseEvent.RegisterAlarm)
        }
    }
}
