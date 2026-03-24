package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.repositoryimpl.TransitsRepositoryImpl
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.CourseInfo
import kotlinx.coroutines.flow.Flow
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
        )
    }

//    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
//        return when (errorCode) {
//            "REQ_001" -> TODO()
//            "REQ_002" -> TODO()
//            "TOK_002" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED, route = Route.Login)
//            "USR_002" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = API_ERROR_INVALID_LOCATION, route = Route.Back)
//            "LOC_003" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = API_ERROR_INVALID_LOCATION, route = Route.Back)
//            "LOC_004" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = API_ERROR_INVALID_LOCATION, route = Route.Back)
//            "TRS_001" -> TODO()
//            "TRS_011" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = API_ERROR_SHORT_DISTANCE, route = Route.Back)
//            "TRS_012" -> ErrorControlFailureException.NavigateAndShowToastException(toastMessage = API_ERROR_OUT_OF_SERVICE_REGION, route = Route.Back)
//            "LRT_003" -> ErrorControlFailureException.SetUIStateException(errorCode = "LRT_003")
//            "LRT_004" -> ErrorControlFailureException.SetUIStateException(errorCode = "LRT_004")
//            "INTERNAL_SERVER_ERROR" -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_NETWORK_FAILURE)
//            else -> ErrorControlFailureException.ShowToastException(API_ERROR_NETWORK_FAILURE)
//        }
//    }
}
