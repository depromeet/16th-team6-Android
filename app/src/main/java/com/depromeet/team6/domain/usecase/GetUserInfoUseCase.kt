package com.depromeet.team6.domain.usecase

import androidx.annotation.Nullable
import com.depromeet.team6.data.repositoryimpl.AuthRepositoryImpl
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.UserInfo
import com.depromeet.team6.domain.repository.AuthRepository
import com.depromeet.team6.domain.usecase.base.ApiRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
import com.depromeet.team6.presentation.ui.home.HomeContract
import com.depromeet.team6.presentation.util.base.UiState
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
            "REQ_001" -> ErrorControlFailureException.SetUIStateException() // TODO : crashlytics, 디스코드/슬랙으로 전송
            "REQ_002" -> ErrorControlFailureException.SetUIStateException() // TODO : crashlytics, 디스코드/슬랙으로 전송
            "TOK_001" -> ErrorControlFailureException.SetUIStateException() // TODO : http 인터셉터에서 리이슈 자동화
            "TOK_002" -> ErrorControlFailureException.SetUIStateException() // TODO : http 인터셉터에서 리이슈 자동화
            "USR_002" -> ErrorControlFailureException.NavigateAndShowToastException("로그인 정보가 만료되었습니다. 다시 로그인 해주세요.", Route.Login) // 토스트 메시지 & 로그인 화면으로 이동
            "INTERNAL_SERVER_ERROR" -> ErrorControlFailureException.ShowToastException("알 수 없는 서버 에러입니다.") // 토스트 메시지 : "알 수 없는 서버 에러입니다. "
            else -> ErrorControlFailureException.ShowToastException("알 수 없음")
        }
    }

}
