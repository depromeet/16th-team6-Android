package com.depromeet.team6.presentation.ui.home

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.data.repositoryimpl.UserInfoRepositoryImpl
import com.depromeet.team6.domain.usecase.DeleteWithDrawUseCase
import com.depromeet.team6.domain.usecase.DummyUseCase
import com.depromeet.team6.domain.usecase.GetLogoutUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dummyUseCase: DummyUseCase,
    private val userInfoRepositoryImpl: UserInfoRepositoryImpl,
    private val getLogoutUseCase: GetLogoutUseCase,
    private val deleteWithDrawUseCase: DeleteWithDrawUseCase
) : BaseViewModel<HomeContract.HomeUiState, HomeContract.HomeSideEffect, HomeContract.HomeEvent>() {
    override fun createInitialState(): HomeContract.HomeUiState = HomeContract.HomeUiState()

    override suspend fun handleEvent(event: HomeContract.HomeEvent) {
        when (event) {
            is HomeContract.HomeEvent.DummyEvent -> setState { copy(loadState = event.loadState) }
            is HomeContract.HomeEvent.LogoutClicked -> logout()
            is HomeContract.HomeEvent.WithDrawClicked -> withDraw()
        }
    }

    fun dummyFunction() {
        viewModelScope.launch {
            setEvent(HomeContract.HomeEvent.DummyEvent(loadState = LoadState.Loading))
            dummyUseCase()
                .onSuccess { data ->
                    setState { copy(loadState = LoadState.Success, dummyData = data) }
                }
                .onFailure {
                    setEvent(
                        HomeContract.HomeEvent.DummyEvent(loadState = LoadState.Error)
                    )
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            getLogoutUseCase().onSuccess {
                setEvent(HomeContract.HomeEvent.LogoutClicked(loadState = LoadState.Error))
                userInfoRepositoryImpl.clear()
                setState { copy(loadState = LoadState.Error) }
            }.onFailure {
                setEvent(HomeContract.HomeEvent.LogoutClicked(loadState = LoadState.Idle))
            }
        }
    }

    fun withDraw() {
        viewModelScope.launch {
            deleteWithDrawUseCase().onSuccess {
                userInfoRepositoryImpl.clear()
                setEvent(HomeContract.HomeEvent.WithDrawClicked(loadState = LoadState.Error))
            }.onFailure {
                setEvent(HomeContract.HomeEvent.WithDrawClicked(loadState = LoadState.Idle))
            }
        }
    }
}
