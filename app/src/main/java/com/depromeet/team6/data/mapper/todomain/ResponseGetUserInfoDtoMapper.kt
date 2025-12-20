package com.depromeet.team6.data.mapper.todomain

import com.depromeet.team6.data.dataremote.model.response.user.ResponseGetUserInfoDto
import com.depromeet.team6.domain.model.GetUserInfo
import com.google.android.gms.maps.model.LatLng

fun ResponseGetUserInfoDto.toDomain(): GetUserInfo {
    return GetUserInfo(
        id = this.id,
        providerId = this.providerId,
        address = this.address,
        userHome = LatLng(this.lat, this.lon),
        appVersion = this.appVersion
    )
}
