package com.depromeet.team6.presentation.ui.coursesearch

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.usecase.LoadMockSearchDataUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseSearchViewModel @Inject constructor(
    private val loadMockData: LoadMockSearchDataUseCase
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
        }
    }

    fun getMockData() {
        val mockData = loadMockData()
        setEvent(CourseSearchContract.CourseEvent.LoadCourseSearchResult(mockData))
    }

    fun setDepartureDestination(departure: String, destination: String) {
        setState {
            copy(
                startingPoint = departure,
                destinationPoint = destination
            )
        }
    }

    fun registerAlarm() {
        viewModelScope.launch {
            setEvent(CourseSearchContract.CourseEvent.RegisterAlarm)
        }
    }
}
