package com.depromeet.team6.presentation.ui.coursesearch

import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.repository.UserInfoRepository
import com.depromeet.team6.domain.usecase.GetCourseSearchResultsUseCase
import com.depromeet.team6.domain.usecase.PostAlarmUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseSearchViewModel @Inject constructor(
    private val loadSearchResult: GetCourseSearchResultsUseCase,
    private val postAlarmUseCase: PostAlarmUseCase,
    private val userInfoRepository: UserInfoRepository
) : BaseViewModel<CourseSearchContract.CourseUiState, CourseSearchContract.CourseSideEffect, CourseSearchContract.CourseEvent>() {
    override fun createInitialState(): CourseSearchContract.CourseUiState = CourseSearchContract.CourseUiState()

    override suspend fun handleEvent(event: CourseSearchContract.CourseEvent) {
        when (event) {
            is CourseSearchContract.CourseEvent.RegisterAlarm -> {
                setSideEffect(CourseSearchContract.CourseSideEffect.ShowNotificationToast)
            }
            is CourseSearchContract.CourseEvent.LoadCourseSearchResult -> setState {
                copy(
                    courseDataLoadState = LoadState.Success,
                    courseData = event.searchResult
                )
            }
            is CourseSearchContract.CourseEvent.InitiateDepartureDestinationPoint -> {
                setState {
                    copy(
                        startingPoint = event.departurePoint,
                        destinationPoint = event.destinationPoint
                    )
                }
                getSearchResults()
            }

            is CourseSearchContract.CourseEvent.InitUiState -> setDepartureDestination(event.departure, event.destination)
            is CourseSearchContract.CourseEvent.ItemCourseDetailToggleClick -> TODO()
            is CourseSearchContract.CourseEvent.ItemCardClick -> TODO()
        }
    }

    private fun getSearchResults() {
        setState {
            copy(
                courseDataLoadState = LoadState.Loading
            )
        }
        viewModelScope.launch {
            loadSearchResult(
                startPoint = uiState.value.startingPoint!!,
                endPoint = uiState.value.destinationPoint!!
            )
                .onSuccess {
                    setEvent(CourseSearchContract.CourseEvent.LoadCourseSearchResult(it))
                }
                .onFailure {
                    setState {
                        copy(
                            courseDataLoadState = LoadState.Error
                        )
                    }
                    setSideEffect(CourseSearchContract.CourseSideEffect.ShowSearchFailedToast(it.message!!))
                }
        }
    }

    private fun setDepartureDestination(departure: String, destination: String) {
        val departurePoint = Gson().fromJson(departure, Address::class.java)
        val destinationPoint = Gson().fromJson(destination, Address::class.java)
        setState {
            copy(
                startingPoint = departurePoint,
                destinationPoint = destinationPoint
            )
        }
        getSearchResults()
    }

    fun postAlarm(lastRouteId: String) {
        viewModelScope.launch {
            if (postAlarmUseCase(
                    lastRouteId = lastRouteId
                ).isSuccessful
            ) {
                setEvent(CourseSearchContract.CourseEvent.RegisterAlarm)
                setSideEffect(CourseSearchContract.CourseSideEffect.NavigateHomeWithToast)
            } else {
                setEvent(CourseSearchContract.CourseEvent.RegisterAlarm)
            }
        }
    }
}
