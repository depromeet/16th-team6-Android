package com.depromeet.team6.presentation.ui.onboarding

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.usecase.DummyUseCase
import com.depromeet.team6.presentation.type.OnboardingType
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val dummyUseCase: DummyUseCase
) : BaseViewModel<OnboardingContract.OnboardingUiState, OnboardingContract.OnboardingSideEffect, OnboardingContract.OnboardingEvent>() {
    override fun createInitialState(): OnboardingContract.OnboardingUiState =
        OnboardingContract.OnboardingUiState()

    override suspend fun handleEvent(event: OnboardingContract.OnboardingEvent) {
        when (event) {
            is OnboardingContract.OnboardingEvent.DummyEvent -> setState { copy(loadState = event.loadState) }
            OnboardingContract.OnboardingEvent.ChangeOnboardingType -> setState { copy(onboardingType = OnboardingType.ALARM) }
        }
    }

    fun dummyFunction() {
        viewModelScope.launch {
            setEvent(OnboardingContract.OnboardingEvent.DummyEvent(loadState = LoadState.Loading))
            dummyUseCase()
                .onSuccess { data ->
                    setState { copy(loadState = LoadState.Success, dummyData = data) }
                }
                .onFailure {
                    setEvent(
                        OnboardingContract.OnboardingEvent.DummyEvent(loadState = LoadState.Error)
                    )
                }
        }
    }
}
