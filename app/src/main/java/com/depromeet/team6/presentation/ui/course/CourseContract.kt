package com.depromeet.team6.presentation.ui.course

import com.depromeet.team6.presentation.model.course.LastTransportInfo
import com.depromeet.team6.presentation.util.base.UiEvent
import com.depromeet.team6.presentation.util.base.UiSideEffect
import com.depromeet.team6.presentation.util.base.UiState
import com.depromeet.team6.presentation.util.view.LoadState

class CourseContract {
    data class CourseUiState(
        val courseDataLoadState: LoadState = LoadState.Idle,
        val courseData: List<LastTransportInfo>
    ) : UiState

    sealed interface CourseSideEffect : UiSideEffect {
        data object ShowNotificationToast : CourseSideEffect
    }

    sealed class CourseEvent : UiEvent {
        data object SetNotification : CourseEvent()
    }
}
