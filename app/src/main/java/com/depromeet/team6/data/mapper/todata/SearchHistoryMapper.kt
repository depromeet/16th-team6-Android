package com.depromeet.team6.data.mapper.todata

import com.depromeet.team6.data.dataremote.model.request.search.RequestSearchHistoryDto
import com.depromeet.team6.domain.model.SearchHistory

fun SearchHistory.toData(): RequestSearchHistoryDto = RequestSearchHistoryDto(
    name = this.name,
    lat = this.lat,
    lon = this.lon,
    businessCategory = this.businessCategory,
    address = this.address
)