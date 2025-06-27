package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.repositoryimpl.TransitsRepositoryImpl
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.usecase.base.ApiRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import javax.inject.Inject

class GetCourseSearchResultsUseCase @Inject constructor(
    private val repository: TransitsRepositoryImpl
) : ApiRequestUseCase<GetCourseSearchResultsUseCase.Params, List<CourseInfo>>() {

    data class Params(val startPoint: Address, val endPoint: Address, val sortType: Int)

    suspend operator fun invoke(
        startPoint: Address,
        endPoint: Address,
        sortType: Int
    ): Result<List<CourseInfo>> = invoke(Params(startPoint, endPoint, sortType))

    override suspend fun apiCall(params: Params): Result<List<CourseInfo>> {
        return repository.getAvailableCourses(
            startPosition = params.startPoint,
            endPosition = params.endPoint,
            sortType = params.sortType
        )
    }

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        return when (errorCode) {
            "TRS_001" -> ErrorControlFailureException.ShowToastException("알 수 없음")
            else -> ErrorControlFailureException.ShowToastException("알 수 없음")
        }
    }
}
