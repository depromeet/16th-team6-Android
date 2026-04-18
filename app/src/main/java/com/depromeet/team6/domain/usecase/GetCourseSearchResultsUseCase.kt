package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.repositoryimpl.TransitsRepositoryImpl
import com.depromeet.team6.domain.ToastMessage.API_ERROR_INVALID_LOCATION
import com.depromeet.team6.domain.ToastMessage.API_ERROR_LOGIN_TOKEN_EXPIRED
import com.depromeet.team6.domain.ToastMessage.API_ERROR_NETWORK_FAILURE
import com.depromeet.team6.domain.ToastMessage.API_ERROR_OUT_OF_SERVICE_REGION
import com.depromeet.team6.domain.ToastMessage.API_ERROR_SHORT_DISTANCE
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import com.depromeet.team6.domain.usecase.base.FlowNetworkRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCourseSearchResultsUseCase @Inject constructor(
    private val repository: TransitsRepositoryImpl
) : FlowNetworkRequestUseCase<CourseInfo>() {

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException =
        when (errorCode) {
            "TOK_002" -> ErrorControlFailureException.NavigateAndShowToastException(
                toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED,
                route = Route.Login
            )

            "USR_002", "LOC_003", "LOC_004" -> ErrorControlFailureException.NavigateAndShowToastException(
                toastMessage = API_ERROR_INVALID_LOCATION,
                route = Route.Back
            )

            "TRS_011" -> ErrorControlFailureException.NavigateAndShowToastException(
                toastMessage = API_ERROR_SHORT_DISTANCE,
                route = Route.Back
            )

            "TRS_012" -> ErrorControlFailureException.NavigateAndShowToastException(
                toastMessage = API_ERROR_OUT_OF_SERVICE_REGION,
                route = Route.Back
            )

            "LRT_003", "LRT_004" -> ErrorControlFailureException.SetUIStateException(errorCode = errorCode)

            "INTERNAL_SERVER_ERROR" -> ErrorControlFailureException.ShowToastException(
                toastMessage = API_ERROR_NETWORK_FAILURE
            )

            else -> ErrorControlFailureException.ShowToastException(API_ERROR_NETWORK_FAILURE)
        }

    operator fun invoke(
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
