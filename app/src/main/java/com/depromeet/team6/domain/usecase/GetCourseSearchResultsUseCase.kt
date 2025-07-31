package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.repositoryimpl.TransitsRepositoryImpl
import com.depromeet.team6.domain.ToastMessage.API_ERROR_INVALID_LOCATION
import com.depromeet.team6.domain.ToastMessage.API_ERROR_LOGIN_TOKEN_EXPIRED
import com.depromeet.team6.domain.ToastMessage.API_ERROR_NETWORK_FAILURE
import com.depromeet.team6.domain.ToastMessage.API_ERROR_OUT_OF_SERVICE_REGION
import com.depromeet.team6.domain.ToastMessage.API_ERROR_SHORT_DISTANCE
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
            "TOK_002" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED, route = Route.Login)
            "USR_002" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = API_ERROR_INVALID_LOCATION, route = Route.Back)
            "LOC_003" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = API_ERROR_INVALID_LOCATION, route = Route.Back)
            "LOC_004" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = API_ERROR_INVALID_LOCATION, route = Route.Back)
            "TRS_001" -> TODO()
            "TRS_011" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = API_ERROR_SHORT_DISTANCE, route = Route.Back)
            "TRS_012" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = API_ERROR_OUT_OF_SERVICE_REGION, route = Route.Back)
            "INTERNAL_SERVER_ERROR" -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_NETWORK_FAILURE)
            else -> ErrorControlFailureException.ShowToastException(API_ERROR_NETWORK_FAILURE)
        }
    }
}
