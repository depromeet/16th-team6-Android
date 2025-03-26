package com.depromeet.team6.presentation.mapper

fun List<com.depromeet.team6.domain.model.Location>.toPresentationList(): List<com.depromeet.team6.presentation.model.location.Location> =
    this.map { domainLocation ->
        com.depromeet.team6.presentation.model.location.Location(
            name = domainLocation.name,
            lat = domainLocation.lat,
            lon = domainLocation.lon,
            businessCategory = domainLocation.businessCategory,
            address = domainLocation.address,
            radius = domainLocation.radius
        )
    }
