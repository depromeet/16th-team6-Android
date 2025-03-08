package com.depromeet.team6.presentation.ui.coursesearch

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.usecase.DummyUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseSearchViewModel @Inject constructor(
    private val dummyUseCase: DummyUseCase
) : BaseViewModel<CourseSearchContract.CourseUiState, CourseSearchContract.CourseSideEffect, CourseSearchContract.CourseEvent>() {
    override fun createInitialState(): CourseSearchContract.CourseUiState = CourseSearchContract.CourseUiState()

    override suspend fun handleEvent(event: CourseSearchContract.CourseEvent) {
        when (event) {
            is CourseSearchContract.CourseEvent.RegisterAlarm -> CourseSearchContract.CourseSideEffect.ShowNotificationToast
        }
    }

    fun registerAlarm() {
        viewModelScope.launch {
            setEvent(CourseSearchContract.CourseEvent.RegisterAlarm)
        }
    }
}
