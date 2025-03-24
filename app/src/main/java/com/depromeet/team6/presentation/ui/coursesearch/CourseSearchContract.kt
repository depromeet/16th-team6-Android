package com.depromeet.team6.presentation.ui.coursesearch

import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.model.course.WayPoint
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class CourseSearchContract {
    data class CourseUiState(
        val courseDataLoadState: LoadState = LoadState.Idle,
        val startingPoint: WayPoint? = null,
        val destinationPoint: WayPoint? = null,
        val courseData: List<CourseInfo> = emptyList()
    ) : UiState

    sealed interface CourseSideEffect : UiSideEffect {
        data object ShowNotificationToast : CourseSideEffect
        data object ShowSearchFailedToast : CourseSideEffect
    }

    sealed class CourseEvent : UiEvent {
        data object RegisterAlarm : CourseEvent()
        data class LoadCourseSearchResult(val searchResult: List<CourseInfo>) : CourseEvent()
        data class InitiateDepartureDestinationPoint(val departurePoint: WayPoint, val destinationPoint: WayPoint) : CourseEvent()
    }
}
