package com.depromeet.team6.data.mapper.todata

import com.depromeet.team6.data.dataremote.model.request.RequestSignUpDto
import com.depromeet.team6.domain.model.SignUp

fun SignUp.toData(): RequestSignUpDto = RequestSignUpDto(
    provider = this.provider,
    address = this.address,
    lat = this.lat,
    lon = this.lon,
    alertAgreement = this.alertAgreement,
    trackingAgreement = this.trackingAgreement,
    alertFrequencies = this.alertFrequencies
)
