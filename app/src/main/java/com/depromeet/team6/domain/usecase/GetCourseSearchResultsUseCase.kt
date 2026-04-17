package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.repositoryimpl.TransitsRepositoryImpl
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.usecase.base.FlowNetworkRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCourseSearchResultsUseCase @Inject constructor(
    private val repository: TransitsRepositoryImpl
) : FlowNetworkRequestUseCase<CourseInfo>() {

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException =
        when (errorCode) {
            "LRT_003", "LRT_004" -> ErrorControlFailureException.SetUIStateException(errorCode)
            else -> ErrorControlFailureException.ShowToastException("경로 검색에 실패했습니다.")
        }

    suspend operator fun invoke(
        startPoint: Address,
        endPoint: Address,
        sortType: Int
    ): Flow<CourseInfo> {
        return repository.getAvailableCoursesStream(
            startPosition = startPoint,
            endPosition = endPoint,
            sortType = sortType
        ).mapNetworkErrors()
    }
}
