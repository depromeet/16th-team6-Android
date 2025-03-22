package com.depromeet.team6.data.mapper.todomain

import com.depromeet.team6.data.dataremote.model.response.locations.ResponseAddressDto
import com.depromeet.team6.domain.model.Address

fun ResponseAddressDto.toDomain(): Address = Address(
//    name = this.name,
//    lat = this.lat,
//    lon = this.lon,
//    address = this.address
    name = "테스트 이름",
    lat = this.lat,
    lon = this.lon,
    address = "테스트 address"
)
