package com.depromeet.team6.presentation.ui.lock

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.usecase.GetTaxiCostUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LockViewModel @Inject constructor(
    private val getTaxiCostUseCase: GetTaxiCostUseCase
) : BaseViewModel<LockContract.LockUiState,
    LockContract.LockSideEffect,
    LockContract.LockEvent>() {

    override fun createInitialState(): LockContract.LockUiState = LockContract.LockUiState()

    init {
        startTimer()
    }

    fun setTaxiCost(taxiCost: Int) {
        setState { copy(taxiCost = taxiCost) }
    }

    private fun startTimer() {
        viewModelScope.launch {
            var currentTime = LockContract.LockUiState.INITIAL_TIME
            while (currentTime > 0) {
                delay(1000L)
                currentTime--
                setState { copy(timeLeft = currentTime) }
            }
            setState { copy(isTimerFinished = true) }
        }
    }

    override suspend fun handleEvent(event: LockContract.LockEvent) {
        when (event) {
            is LockContract.LockEvent.OnTimerFinish -> {
                setSideEffect(LockContract.LockSideEffect.CloseScreen)
            }
            is LockContract.LockEvent.OnCloseClick -> {
                if (currentState.isTimerFinished) {
                    setSideEffect(LockContract.LockSideEffect.CloseScreen)
                }
            }
        }
    }

    fun loadTaxiCost() {
        viewModelScope.launch {
            try {
                val cost = getTaxiCostUseCase.getLastSavedTaxiCost()
                setState {
                    copy(taxiCost = cost)
                }
            } catch (e: Exception) {
                Log.e(TAG, "택시 비용 로드 중 오류 발생", e)
            }
        }
    }

}
