package com.depromeet.team6.presentation.ui.lock

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.data.background.AlarmScheduler
import com.depromeet.team6.domain.repository.HomeRepository
import com.depromeet.team6.domain.repository.UserInfoRepository
import com.depromeet.team6.domain.usecase.GetTaxiCostUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LockViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val userInfoRepository: UserInfoRepository,
    private val getTaxiCostUseCase: GetTaxiCostUseCase,
    @ApplicationContext private val context: Context
) : BaseViewModel<LockContract.LockUiState,
    LockContract.LockSideEffect,
    LockContract.LockEvent>() {

    override fun createInitialState(): LockContract.LockUiState = LockContract.LockUiState()

    init {
        startTimer()
        loadTaxiCost()
    }

    fun setTaxiCost(taxiCost: Int) {
        if (taxiCost > 0) {
            setState { copy(taxiCost = taxiCost) }
            viewModelScope.launch {
                getTaxiCostUseCase.saveTaxiCost(taxiCost)
            }
        } else {
            loadTaxiCost()
        }
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
            LockContract.LockEvent.OnDepartureClick -> {
                stopAlarmFlow()
                setSideEffect(LockContract.LockSideEffect.NavigateToHome(true))
            }
            LockContract.LockEvent.OnLateClick -> {
                stopAlarmFlow()
                setSideEffect(LockContract.LockSideEffect.NavigateToHome(false))
            }
        }
    }

    fun getUserId(): Int {
        return userInfoRepository.getUserID()
    }

    fun loadTaxiCost() {
        viewModelScope.launch {
            try {
                val cost = getTaxiCostUseCase.getPersistedTaxiCostForLockScreen()
                if (cost > 0) {
                    setState {
                        copy(taxiCost = cost)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "택시 비용 로드 중 오류 발생", e)
            }
        }
    }

    /**
     * 사용자 출발 상태 플래그 설정 + 알람 전체 취소를 동기적으로 수행한다.
     * 내부 viewModelScope.launch {} 래퍼를 제거하여 Activity.finish() 전에
     * 반드시 완료되도록 보장한다(race condition 방지).
     */
    private fun stopAlarmFlow() {
        try {
            homeRepository.setUserDeparture(true)
            AlarmScheduler.unScheduleAllAlarms(context)
            Log.d("userDeparture", "사용자 출발 처리 및 알람 정리 완료")
        } catch (e: Exception) {
            Log.e("userDeparture", "사용자 출발 처리 중 오류 발생", e)
        }
    }

    fun finishAlarmAndResetState() {
        viewModelScope.launch {
            try {
                homeRepository.clearAlarmData()
                homeRepository.clearUserDeparture()
                AlarmScheduler.unScheduleAllAlarms(context)
            } catch (e: Exception) {
                Log.e("LockViewModel", "알람 종료 및 상태 초기화 중 오류 발생", e)
            }
        }
    }
}
