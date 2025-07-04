package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.repositoryimpl.AuthRepositoryImpl
import com.depromeet.team6.domain.model.UserInfo
import com.depromeet.team6.domain.usecase.base.ApiRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepositoryImpl
) : ApiRequestUseCase<Unit, UserInfo>() {

    suspend operator fun invoke(): Result<UserInfo> =
        invoke(Unit)

    override suspend fun apiCall(params: Unit): Result<UserInfo> {
        return authRepository.getUserInfo()
    }

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        return when (errorCode) {
            "TOK_001" -> ErrorControlFailureException.NavigateAndShowToastException("로그인 정보가 만료되었습니다. 다시 로그인 해주세요.", Route.Login)
            "TOK_002" -> ErrorControlFailureException.NavigateAndShowToastException("로그인 정보가 만료되었습니다. 다시 로그인 해주세요.", Route.Login)
            "USR_002" -> ErrorControlFailureException.NavigateAndShowToastException("로그인 정보가 만료되었습니다. 다시 로그인 해주세요.", Route.Login)
            "INTERNAL_SERVER_ERROR" -> ErrorControlFailureException.ShowToastException("알 수 없는 서버 에러입니다.")
            else -> ErrorControlFailureException.ShowToastException("알 수 없는 서버 에러입니다.")
        }
    }
}
