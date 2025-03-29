package com.depromeet.team6.domain.model

import com.google.android.gms.maps.model.LatLng

data class Auth(
    val id: Int,
    val accessToken: String,
    val refreshToken: String,
    val userHome: LatLng
)
