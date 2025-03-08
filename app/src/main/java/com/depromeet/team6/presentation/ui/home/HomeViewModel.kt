package com.depromeet.team6.presentation.ui.home

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.data.repositoryimpl.UserInfoRepositoryImpl
import com.depromeet.team6.domain.usecase.DeleteWithDrawUseCase
import com.depromeet.team6.domain.usecase.DummyUseCase
import com.depromeet.team6.domain.usecase.GetAddressFromCoordinatesUseCase
import com.depromeet.team6.domain.usecase.PostLogoutUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.android.gms.maps.model.LatLng
import com.depromeet.team6.presentation.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dummyUseCase: DummyUseCase,
    private val userInfoRepositoryImpl: UserInfoRepositoryImpl,
    private val postLogoutUseCase: PostLogoutUseCase,
    private val deleteWithDrawUseCase: DeleteWithDrawUseCase,
    private val getAddressFromCoordinatesUseCase: GetAddressFromCoordinatesUseCase
) : BaseViewModel<HomeContract.HomeUiState, HomeContract.HomeSideEffect, HomeContract.HomeEvent>() {
class HomeViewModel @Inject constructor() : BaseViewModel<HomeContract.HomeUiState, HomeContract.HomeSideEffect, HomeContract.HomeEvent>() {

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

    fun logout() {
        Log.d("Logout Reponse", "Logout Response: $postLogoutUseCase")
        userInfoRepositoryImpl.setAccessToken(userInfoRepositoryImpl.getRefreshToken())
        viewModelScope.launch {
            if (postLogoutUseCase().isSuccessful) {
                setEvent(HomeContract.HomeEvent.LogoutClicked(loadState = LoadState.Error))
                userInfoRepositoryImpl.clear()
            } else {
                setEvent(HomeContract.HomeEvent.LogoutClicked(loadState = LoadState.Idle))
            }
        }
    }

    fun withDraw() {
        viewModelScope.launch {
            if (deleteWithDrawUseCase().isSuccessful) {
                userInfoRepositoryImpl.clear()
                setEvent(HomeContract.HomeEvent.WithDrawClicked(loadState = LoadState.Error))
            } else {
                setEvent(HomeContract.HomeEvent.WithDrawClicked(loadState = LoadState.Idle))
            }
        }
    }

    fun getCenterLocation(location: LatLng) {
        viewModelScope.launch {
            getAddressFromCoordinatesUseCase.invoke(location.latitude, location.longitude)
                .onSuccess {
                    setState {
                        copy(
                            locationAddress = it.name
                        )
                    }
                }.onFailure {
                    setState {
                        copy(
                            locationAddress = ""
                        )
                    }
                }
        }
    }
}
