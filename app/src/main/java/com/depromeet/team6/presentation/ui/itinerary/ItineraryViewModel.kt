package com.depromeet.team6.presentation.ui.itinerary

import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ItineraryViewModel @Inject constructor() : BaseViewModel<ItineraryContract.ItineraryUiState, ItineraryContract.ItinerarySideEffect, ItineraryContract.ItineraryEvent>() {
    override fun createInitialState(): ItineraryContract.ItineraryUiState = ItineraryContract.ItineraryUiState()

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
            ItineraryContract.ItineraryEvent.RegisterAlarm -> TODO()
        }
    }

    fun initItineraryInfo(courseInfoJSON: String) {
        val courseInfo = Gson().fromJson(courseInfoJSON, CourseInfo::class.java)
        setState {
            copy(
                courseDataLoadState = LoadState.Success,
                itineraryInfo = courseInfo
            )
        }
    }
//    fun getLegs() {
//        val mockData = getCourseSearchResults()
//        setEvent(ItineraryContract.ItineraryEvent.LoadLegsResult(mockData[0]))
//    }
}
