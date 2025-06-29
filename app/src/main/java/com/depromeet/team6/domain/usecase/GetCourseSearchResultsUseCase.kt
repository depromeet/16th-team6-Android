package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.repositoryimpl.TransitsRepositoryImpl
import com.depromeet.team6.domain.ToastMessage.LOGIN_DATA_EXPIRED
import com.depromeet.team6.domain.ToastMessage.NETWORK
import com.depromeet.team6.domain.ToastMessage.OUT_OF_RANGE
import com.depromeet.team6.domain.ToastMessage.RANGE_LOCATION
import com.depromeet.team6.domain.ToastMessage.SHORT_DISTANCE
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.usecase.base.ApiRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
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
            "REQ_001" -> TODO()
            "REQ_002" -> TODO()
            "TOK_002" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = LOGIN_DATA_EXPIRED, route = Route.Login)
            "USR_002" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = RANGE_LOCATION, route = Route.Home)
            "LOC_003" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = RANGE_LOCATION, route = Route.Home)
            "LOC_004" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = RANGE_LOCATION, route = Route.Home)
            "TRS_001" -> TODO()
            "TRS_011" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = SHORT_DISTANCE, route = Route.Home)
            "TRS_012" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = OUT_OF_RANGE, route = Route.Home)
            "INTERNAL_SERVER_ERROR" -> ErrorControlFailureException.ShowToastException(toastMessage = NETWORK)
            else -> ErrorControlFailureException.ShowToastException(NETWORK)
        }
    }
}
