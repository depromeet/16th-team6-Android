package com.depromeet.team6.data.mapper.todomain

import com.depromeet.team6.data.dataremote.model.response.ResponseCheckDto

fun ResponseCheckDto.toDomain(): Boolean = this.exists
