package com.depromeet.team6.data.mapper.todata

import com.depromeet.team6.data.dataremote.model.request.RequestLoginDto
import com.depromeet.team6.domain.model.Login

fun Login.toData(): RequestLoginDto = RequestLoginDto(
    platform = this.platform
)
