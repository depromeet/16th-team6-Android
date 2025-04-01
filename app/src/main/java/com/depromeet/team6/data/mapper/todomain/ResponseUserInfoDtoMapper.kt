package com.depromeet.team6.data.mapper.todomain

import com.depromeet.team6.data.dataremote.model.response.user.ResponseUserInfoDto
import com.depromeet.team6.domain.model.UserInfo
import com.google.android.gms.maps.model.LatLng

fun ResponseUserInfoDto.toDomain(): UserInfo {
    return UserInfo(
        id = this.id,
        providerId = this.providerId,
        nickname = this.nickname,
        profileImageUrl = this.profileImageUrl,
        address = this.address,
        userHome = LatLng(this.lat, this.lon),
        alertFrequencies = this.alertFrequencies
    )
}
