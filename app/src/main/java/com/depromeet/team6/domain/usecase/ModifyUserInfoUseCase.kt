package com.depromeet.team6.domain.usecase

import com.depromeet.team6.data.dataremote.model.request.user.RequestModifyUserInfoDto
import com.depromeet.team6.data.repositoryimpl.AuthRepositoryImpl
import com.depromeet.team6.domain.model.UserInfo
import com.depromeet.team6.domain.repository.AuthRepository
import com.depromeet.team6.domain.usecase.base.ApiRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
import javax.inject.Inject

class ModifyUserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepositoryImpl
) : ApiRequestUseCase<ModifyUserInfoUseCase.Params, UserInfo>()
{
    data class Params(val modifyUserInfoDto: RequestModifyUserInfoDto)


    suspend operator fun invoke(modifyUserInfoDto: RequestModifyUserInfoDto): Result<UserInfo> =
        authRepository.modifyUserInfo(modifyUserInfoDto = modifyUserInfoDto)

    override suspend fun apiCall(params: Params): Result<UserInfo> {
        return authRepository.modifyUserInfo(
            params.modifyUserInfoDto
        )
    }

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        return when (errorCode) {
            "REQ_001" -> ErrorControlFailureException.ShowToastException("알 수 없는 서버 에러입니다.") // TODO : 디코 알림 전송
            "REQ_002" -> ErrorControlFailureException.ShowToastException("알 수 없는 서버 에러입니다.") // TODO : 디코 알림 전송
            "TOK_001" -> ErrorControlFailureException.NavigateAndShowToastException("로그인 정보가 만료되었습니다. 다시 로그인 해주세요.", Route.Login)
            "TOK_002" -> ErrorControlFailureException.NavigateAndShowToastException("로그인 정보가 만료되었습니다. 다시 로그인 해주세요.", Route.Login)
            "USR_002" -> ErrorControlFailureException.NavigateAndShowToastException("로그인 정보가 만료되었습니다. 다시 로그인 해주세요.", Route.Login)
            "LOC_003" -> ErrorControlFailureException.ShowToastException("유효 범위를 벗어났습니다. 주소를 다시 설정해주세요.")
            "LOC_004" -> ErrorControlFailureException.ShowToastException("유효 범위를 벗어났습니다. 주소를 다시 설정해주세요.")
            "INTERNAL_SERVER_ERROR" -> ErrorControlFailureException.ShowToastException("알 수 없는 서버 에러입니다.")
            else -> ErrorControlFailureException.ShowToastException("알 수 없는 서버 에러입니다.")
        }
    }
}
