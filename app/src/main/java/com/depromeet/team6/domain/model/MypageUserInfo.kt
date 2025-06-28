package com.depromeet.team6.domain.model

data class MypageUserInfo(
    val address: String? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val alertFrequencies: Set<Int>? = null,
    val fcmToken: String? = null
)
