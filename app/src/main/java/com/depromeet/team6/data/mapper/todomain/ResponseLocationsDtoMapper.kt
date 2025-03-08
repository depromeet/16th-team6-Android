package com.depromeet.team6.data.mapper.todomain

import com.depromeet.team6.data.dataremote.model.response.locations.ResponseLocationsDto
import com.depromeet.team6.domain.model.Location

fun List<ResponseLocationsDto>.toDomain(): List<Location> = map { response ->
    Location(
        name = response.name,
        lat = response.lat,
        lon = response.lon,
        businessCategory = response.businessCategory,
        address = response.address,
        radius = response.radius
    )
}
