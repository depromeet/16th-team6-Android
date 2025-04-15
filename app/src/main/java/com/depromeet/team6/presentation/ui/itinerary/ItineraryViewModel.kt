package com.depromeet.team6.presentation.ui.itinerary

import android.util.SparseArray
import androidx.lifecycle.viewModelScope
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.RealTimeBusArrival
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.domain.usecase.GetBusArrivalUseCase
import com.depromeet.team6.presentation.util.base.BaseViewModel
import com.depromeet.team6.presentation.util.view.LoadState
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ItineraryViewModel @Inject constructor(
    private val getBusArrivalUseCase: GetBusArrivalUseCase
) : BaseViewModel<ItineraryContract.ItineraryUiState, ItineraryContract.ItinerarySideEffect, ItineraryContract.ItineraryEvent>() {
    override fun createInitialState(): ItineraryContract.ItineraryUiState = ItineraryContract.ItineraryUiState()

    override suspend fun handleEvent(event: ItineraryContract.ItineraryEvent) {
        when (event) {
            is ItineraryContract.ItineraryEvent.LoadLegsResult -> {
                setState {
                    copy(
                        itineraryInfo = event.result,
                        courseDataLoadState = LoadState.Success
                    )
                }
            }
            is ItineraryContract.ItineraryEvent.RefreshButtonClicked -> getRemainingBusArrivalTimes()
            is ItineraryContract.ItineraryEvent.CurrentLocationClicked -> {
                setState {
                    copy(
                        currentLocation = event.location
                    )
                }
            }
        }
    }

    fun initItineraryInfo(courseInfoJSON: String, departurePointJSON: String, destinationPointJSON: String, currentLocation: LatLng) {
        val courseInfo = Gson().fromJson(courseInfoJSON, CourseInfo::class.java)
        val departurePoint = Gson().fromJson(departurePointJSON, Address::class.java)
        val destinationPoint = Gson().fromJson(destinationPointJSON, Address::class.java)

        setState {
            copy(
                courseDataLoadState = LoadState.Success,
                departurePoint = departurePoint,
                destinationPoint = destinationPoint,
                itineraryInfo = courseInfo,
                currentLocation = currentLocation
            )
        }
        getRemainingBusArrivalTimes()
    }

    private fun getRemainingBusArrivalTimes() {
        val newBusArrivalStatus = SparseArray<RealTimeBusArrival>()

        viewModelScope.launch {
            val deferredList = currentState.itineraryInfo!!.legs.mapIndexedNotNull { idx, leg ->
                if (leg.transportType != TransportType.BUS) return@mapIndexedNotNull null

                async {
                    val result = getBusArrivalUseCase(
                        routeName = leg.routeName!!,
                        stationName = leg.startPoint.name,
                        lat = leg.startPoint.lat,
                        lon = leg.startPoint.lon
                    )
                    result.mapCatching {
                        Timber.d("busArrivalStatusAPIResult : $it")
                        idx to it.realTimeBusArrival[0]
                    }.getOrNull()
                }
            }
            // 모든 API 호출이 끝난 뒤 결과를 처리
            val results = deferredList.awaitAll().filterNotNull()
            for ((idx, arrival) in results) {
                newBusArrivalStatus.put(idx, arrival)
            }
            setState {
                copy(
                    busArrivalStatus = newBusArrivalStatus
                )
            }
        }
        Timber.d("busArrivalStatus ViewModel : ${currentState.busArrivalStatus}")
    }
}
