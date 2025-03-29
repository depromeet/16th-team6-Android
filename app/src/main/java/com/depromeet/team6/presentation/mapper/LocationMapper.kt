package com.depromeet.team6.presentation.mapper

import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.presentation.model.location.Location

fun Location.toAddress(): Address =
    Address(
        name = this.name,
        lat = this.lat,
        lon = this.lon,
        address = this.address
    )
