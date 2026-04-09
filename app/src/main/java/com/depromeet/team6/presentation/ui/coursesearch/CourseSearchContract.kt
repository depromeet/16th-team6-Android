package com.depromeet.team6.presentation.ui.coursesearch

import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class CourseSearchContract {
    data class CourseUiState(
        val courseUiLoadState: LoadState = LoadState.Idle,
        val courseSearchDataLoadState: CourseSearchDataState = CourseSearchDataState.Idle,
        val startingPoint: Address? = null,
        val destinationPoint: Address? = null,
        val courseData: List<CourseInfo> = emptyList(),
        val sortType: Int = 1,
        val showDeleteAlarmDialog: Boolean = false,
        val showOverlayPermissionDialog: Boolean = false,
        val showPermissionSnackbar: Boolean = false,
        val selectedRouteId: String = ""
    ) : UiState

    enum class CourseSearchDataState {
        Idle,
        Loading,
        Success,
        NoResult, // 막차 검색정보가 없음
        ServiceEnded, // 시간이 늦어서 막차가 없음
        Unknown
    }

    sealed interface CourseSideEffect : UiSideEffect {
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
        data object ShowOverlayPermissionDialog : CourseEvent()
        data object DismissOverlayPermissionDialog : CourseEvent()
        data object ShowPermissionSnackbar : CourseEvent()
        data object DismissPermissionSnackbar : CourseEvent()
    }
}
