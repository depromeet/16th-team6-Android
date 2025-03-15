package com.depromeet.team6.data.repositoryimpl

import com.depromeet.team6.data.dataremote.datasource.LocationsRemoteDataSource
import com.depromeet.team6.data.mapper.todomain.toDomain
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.Location
import com.depromeet.team6.domain.repository.LocationsRepository
import javax.inject.Inject

class LocationsRepositoryImpl @Inject constructor(
    private val locationsRemoteDataSource: LocationsRemoteDataSource
) : LocationsRepository {
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
}
