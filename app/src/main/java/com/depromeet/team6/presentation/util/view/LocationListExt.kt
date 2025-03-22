package com.depromeet.team6.presentation.util.view

import com.depromeet.team6.presentation.model.location.Location

fun List<Location>.partitionByAddressCategory(): Pair<List<Location>, List<Location>> {
    return this.partition { it.businessCategory.startsWith("지역") }
}
