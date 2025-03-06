package com.depromeet.team6.presentation.ui.course

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.usecase.DummyUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val dummyUseCase: DummyUseCase
) : BaseViewModel<CourseContract.CourseUiState, CourseContract.CourseSideEffect, CourseContract.CourseEvent>() {
    override fun createInitialState(): CourseContract.CourseUiState {
        TODO("Not yet implemented")
    }

    override suspend fun handleEvent(event: CourseContract.CourseEvent) {
        when (event) {
            is CourseContract.CourseEvent.SetNotification -> CourseContract.CourseSideEffect.ShowNotificationToast
        }
    }

    fun dummyFunction() {
        viewModelScope.launch {
            setEvent(CourseContract.CourseEvent.SetNotification)
        }
    }
}
