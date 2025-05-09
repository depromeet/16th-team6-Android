package com.depromeet.team6.presentation.ui.coursesearch

import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class CourseSearchContract {
    data class CourseUiState(
        val courseDataLoadState: LoadState = LoadState.Idle,
        val startingPoint: Address? = null,
        val destinationPoint: Address? = null,
        val courseData: List<CourseInfo> = emptyList(),
        val sortType: Int = 1,
        val showDeleteAlarmDialog: Boolean = false,
        val selectedRouteId: String = ""
    ) : UiState

    sealed interface CourseSideEffect : UiSideEffect {
        data object ShowNotificationToast : CourseSideEffect
        data class ShowSearchFailedToast(val message: String) : CourseSideEffect
        data object NavigateHomeWithToast : CourseSideEffect
    }

    sealed class CourseEvent : UiEvent {
        data object RegisterAlarm : CourseEvent()
        data object OnEnter : CourseEvent()
        data object OnExit : CourseEvent()
        data class InitUiState(val departure: String, val destination: String) : CourseEvent()
        data class LoadCourseSearchResult(val searchResult: List<CourseInfo>) : CourseEvent()
        data class InitiateDepartureDestinationPoint(val departurePoint: Address, val destinationPoint: Address) : CourseEvent()
        data object ItemCourseDetailToggleClick : CourseEvent()
        data class ItemCardClick(val isTextClicked: Boolean) : CourseEvent()
        data class ShowDeleteAlarmDialog(val routeId: String) : CourseEvent()
        data object DismissDeleteAlarmDialog : CourseEvent()
    }
}
