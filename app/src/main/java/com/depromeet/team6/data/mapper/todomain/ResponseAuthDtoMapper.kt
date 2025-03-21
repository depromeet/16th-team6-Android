package com.depromeet.team6.data.mapper.todomain

import com.depromeet.team6.data.dataremote.model.response.user.ResponseAuthDto
import com.depromeet.team6.domain.model.Auth
import com.google.android.gms.maps.model.LatLng

fun ResponseAuthDto.toDomain(): Auth = Auth(
    id = this.id,
    accessToken = this.accessToken,
    refreshToken = this.refreshToken,
    userHome = LatLng(this.lat, this.lon)
)
