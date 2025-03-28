package com.depromeet.team6.presentation.ui.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.usecase.GetAddressFromCoordinatesUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAddressFromCoordinatesUseCase: GetAddressFromCoordinatesUseCase
) : BaseViewModel<HomeContract.HomeUiState, HomeContract.HomeSideEffect, HomeContract.HomeEvent>() {
    private var speechBubbleJob: Job? = null

    init {
        showSpeechBubbleTemporarily()
    }

    override fun createInitialState(): HomeContract.HomeUiState = HomeContract.HomeUiState()

    override suspend fun handleEvent(event: HomeContract.HomeEvent) {
        when (event) {
            is HomeContract.HomeEvent.DummyEvent -> setState { copy(loadState = event.loadState) }
            is HomeContract.HomeEvent.UpdateAlarmRegistered -> setState { copy(isAlarmRegistered = event.isRegistered) }
            is HomeContract.HomeEvent.UpdateBusDeparted -> setState { copy(isBusDeparted = event.isBusDeparted) }
            is HomeContract.HomeEvent.UpdateSpeechBubbleVisibility -> setState {
                copy(
                    showSpeechBubble = event.show
                )
            }

            is HomeContract.HomeEvent.OnCharacterClick -> onCharacterClick()
        }
    }

    fun registerAlarm() {
        viewModelScope.launch {
            setEvent(HomeContract.HomeEvent.UpdateAlarmRegistered(true))
        }
    }

    fun finishAlarm(context: Context) {
        viewModelScope.launch {
            // TODO : 알림 등록 API 연동 후 SharedPreferences 로직 삭제
            val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            sharedPreferences.edit().apply {
                putBoolean("isUserLoggedIn", false)
                apply()
            }

            setEvent(HomeContract.HomeEvent.UpdateAlarmRegistered(false))
            setEvent(HomeContract.HomeEvent.UpdateBusDeparted(false))
        }
    }

    fun setBusDeparted() {
        viewModelScope.launch {
            setEvent(HomeContract.HomeEvent.UpdateBusDeparted(true))
        }
    }

    private fun showSpeechBubbleTemporarily() {
        speechBubbleJob?.cancel()

        speechBubbleJob = viewModelScope.launch {
            setEvent(HomeContract.HomeEvent.UpdateSpeechBubbleVisibility(true))
            delay(2500)
            setEvent(HomeContract.HomeEvent.UpdateSpeechBubbleVisibility(false))
        }
    }

    fun onCharacterClick() {
        showSpeechBubbleTemporarily()
    }

    fun getCenterLocation(location: LatLng) {
        viewModelScope.launch {
            getAddressFromCoordinatesUseCase.invoke(location.latitude, location.longitude)
                .onSuccess {
                    setState {
                        copy(
                            departurePoint = it
                        )
                    }
                }.onFailure {
                    setState {
                        // 위치 찾을 수 없는 경우 서울시청으로 임의 초기화
                        copy(
                            departurePoint = Address(
                                name = "서울특별시청",
                                lat = 37.56681744674135,
                                lon = 126.97866075004276,
                                address = ""
                            )
                        )
                    }
                }
        }
    }
}
