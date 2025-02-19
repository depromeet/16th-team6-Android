package com.depromeet.team6.data.mapper.todomain

import com.depromeet.team6.data.dataremote.model.response.ResponseAuthDto
import com.depromeet.team6.domain.model.Auth


fun ResponseAuthDto.toDomain(): Auth = Auth(
    accessToken = this.accessToken,
    refreshToken = this.refreshToken
)
