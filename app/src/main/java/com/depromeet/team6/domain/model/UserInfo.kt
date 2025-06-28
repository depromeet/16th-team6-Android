package com.depromeet.team6.domain.model

import com.google.android.gms.maps.model.LatLng

data class UserInfo(
    val id: Int,
    val providerId: Long,
    val address: String,
    val userHome: LatLng,
    val alertFrequencies: Set<Int>
)
