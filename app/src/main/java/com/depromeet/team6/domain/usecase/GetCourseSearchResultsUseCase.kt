package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.dataremote.model.response.base.ApiException
import com.depromeet.team6.data.repositoryimpl.TransitsRepositoryImpl
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class GetCourseSearchResultsUseCase @Inject constructor(
    private val repository: TransitsRepositoryImpl
) {
    suspend operator fun invoke(
        startPoint: Address,
        endPoint: Address,
        sortType: Int
    ): Flow<CourseInfo> {
        return repository.getAvailableCoursesStream(
            startPosition = startPoint,
            endPosition = endPoint,
            sortType = sortType
        ).catch { exception ->
            throw mapException(exception)
        }
    }

    private fun mapException(exception: Throwable): ErrorControlFailureException =
        when (exception) {
            is ApiException.ApiRequestFailureException -> when (exception.errorCode) {
                "LRT_003", "LRT_004" -> ErrorControlFailureException.SetUIStateException(exception.errorCode)
                else -> ErrorControlFailureException.ShowToastException(exception.errorMessage)
            }
            is ApiException.NetworkFailureException ->
                ErrorControlFailureException.ShowToastException(exception.errorMessage)
            else ->
                ErrorControlFailureException.ShowToastException("알 수 없는 오류가 발생했습니다.")
        }
}
