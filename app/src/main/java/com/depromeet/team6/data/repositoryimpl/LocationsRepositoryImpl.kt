package com.depromeet.team6.data.repositoryimpl

import android.annotation.SuppressLint
import android.content.Context
import android.os.HandlerThread
import com.depromeet.team6.data.dataremote.datasource.LocationsRemoteDataSource
import com.depromeet.team6.data.mapper.todata.toData
import com.depromeet.team6.data.mapper.todomain.toDomain
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.Location
import com.depromeet.team6.domain.model.SearchHistory
import com.depromeet.team6.domain.repository.LocationsRepository
import com.depromeet.team6.presentation.util.permission.PermissionUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LocationsRepositoryImpl @Inject constructor(
    private val locationsRemoteDataSource: LocationsRemoteDataSource,
    @ApplicationContext private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : LocationsRepository {
    @SuppressLint("MissingPermission")
    override fun getRealtimeLocation(): Flow<LatLng> = callbackFlow {
        if (!PermissionUtil.hasLocationPermissions(context)) {
            close(IllegalStateException("Location permission not granted"))
            return@callbackFlow
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L
        ).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    trySend(LatLng(it.latitude, it.longitude))
                }
            }
        }

        val handlerThread = HandlerThread("LocationThread")
        handlerThread.start()
        val looper = handlerThread.looper

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            looper
        )

        awaitClose {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            handlerThread.quitSafely()
        }
    }

    override suspend fun getLocations(
        keyword: String,
        lat: Double,
        lon: Double
    ): Result<List<Location>> =
        locationsRemoteDataSource.getLocations(keyword = keyword, lat = lat, lon = lon)
            .mapCatching { it.toDomain() }

    override suspend fun getAddressFromCoordinates(lat: Double, lon: Double): Result<Address> =
        locationsRemoteDataSource.getAddressFromCoordinates(lat = lat, lon = lon)
            .mapCatching { it.toDomain() }

    override suspend fun getSearchHistories(lat: Double, lon: Double): Result<List<Location>> =
        locationsRemoteDataSource.getSearchHistories(lat = lat, lon = lon)
            .mapCatching { it.toDomain() }

    override suspend fun postSearchHistories(requestSearchHistoryDto: SearchHistory): Result<Unit> =
        locationsRemoteDataSource.postSearchHistories(requestSearchHistoryDto = requestSearchHistoryDto.toData())

    override suspend fun deleteSearchHistory(name: String, lat: Double, lon: Double, businessCategory: String, address: String): Result<Unit> =
        locationsRemoteDataSource.deleteSearchHistory(name = name, lat = lat, lon = lon, businessCategory = businessCategory, address = address)

    override suspend fun deleteAllSearchHistory(): Result<Unit> =
        locationsRemoteDataSource.deleteAllSearchHistory()
}
