package com.depromeet.team6.presentation.ui.bus

import com.depromeet.team6.domain.repository.UserInfoRepository
import com.depromeet.team6.domain.usecase.GetAddressFromCoordinatesUseCase
import com.depromeet.team6.domain.usecase.GetLocationsUseCase
import com.depromeet.team6.domain.usecase.PostSignUpUseCase
import com.depromeet.team6.presentation.ui.onboarding.OnboardingContract
import com.depromeet.team6.presentation.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BusCourseViewModel @Inject constructor(

) : BaseViewModel<BusCourseContract.BusCourseUiState, BusCourseContract.BusCourseSideEffect, BusCourseContract.BusCourseEvent>() {
    override fun createInitialState(): BusCourseContract.BusCourseUiState =
        BusCourseContract.BusCourseUiState()

    override suspend fun handleEvent(event: BusCourseContract.BusCourseEvent) {
        when (event) {
            is BusCourseContract.BusCourseEvent.SetScreenLoadState -> setState { copy(loadState = event.loadState)}
        }
    }
}