package com.depromeet.team6.domain.model.course

import com.depromeet.team6.domain.model.Address

data class LegInfo(
    val transportType: TransportType,
    val subTypeIdx: Int = 0,
    val departureDateTime: String? = "",
    val routeName: String? = null,
    val sectionTime: Int, // 구간 소요시간 (s)
    val distance: Int, // 구간 이동거리 (m)
    val startPoint: Address,
    val endPoint: Address,
    val passShape: String
)
