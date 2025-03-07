package com.depromeet.team6.data.mapper

import com.depromeet.team6.data.dataremote.model.response.dummy.DummyResponseDto
import com.depromeet.team6.domain.model.DummyData

fun DummyResponseDto.toDomainModel(): DummyData {
    return DummyData(
        description = this.dummy + "입니다 "
    )
}
