package com.depromeet.team6.presentation.ui.coursesearch

import com.depromeet.team6.presentation.model.course.LastTransportInfo
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class CourseSearchContract {
    data class CourseUiState(
        val courseDataLoadState: LoadState = LoadState.Idle,
        val startingPoint: String = "",
        val destinationPoint : String = "",
        val courseData: List<LastTransportInfo> = emptyList()
    ) : UiState

    sealed interface CourseSideEffect : UiSideEffect {
        data object ShowNotificationToast : CourseSideEffect
        data object ShowSearchFailedToast : CourseSideEffect
    }

    sealed class CourseEvent : UiEvent {
        data object RegisterAlarm : CourseEvent()
    }
}
