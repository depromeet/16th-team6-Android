package com.depromeet.team6.domain.usecase

import com.depromeet.team6.R
import com.depromeet.team6.data.repositoryimpl.MockSearchDataImpl
import com.depromeet.team6.presentation.model.course.LastTransportInfo
import javax.inject.Inject

class LoadMockSearchDataUseCase @Inject constructor(
    private val repository : MockSearchDataImpl
){
    operator fun invoke(): List<LastTransportInfo> {
        val mockData = repository.loadMockData( R.raw.mock_data)
        return emptyList()
    }
}