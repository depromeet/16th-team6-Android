package com.depromeet.team6.data.dataremote.interceptor

import android.app.Application
import android.content.Intent
import com.depromeet.team6.BuildConfig
import com.depromeet.team6.data.datalocal.datasource.UserInfoLocalDataSource
import com.depromeet.team6.data.dataremote.model.response.base.ApiResponse
import com.depromeet.team6.data.dataremote.model.response.user.ResponseReissueDto
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import com.depromeet.team6.data.dataremote.util.ApiConstraints.AUTH
import com.depromeet.team6.data.dataremote.util.ApiConstraints.REISSUE
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val json: Json,
    private val localStorage: UserInfoLocalDataSource,
    private val context: Application
) : Interceptor {

    private val mutex = Mutex()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val authRequest =
            if (localStorage.accessToken.isNotBlank()) originalRequest.newAuthBuilder() else originalRequest
        var response = chain.proceed(authRequest)

        Timber.d("API_REQUEST : $originalRequest")
        Timber.d("API_RESPONSE : $response")
        if (response.code == HTTP_BAD_REQUEST) {
            // errorBody를 문자열로 읽어 소비합니다. (더이상 response를 사용할 수 없게 되기에 복제해야함)
            val errorBodyString = response.body?.string()
            // errorBody 복제를 위해 기존의 ContentType을 가져옵니다.
            val contentType = response.body?.contentType()
            // 읽어온 문자열을 기반으로 새로운 ResponseBody를 생성합니다.
            val newErrorBody = errorBodyString?.toResponseBody(contentType)

            val errorResponse = Gson().fromJson(errorBodyString, ApiResponse::class.java)

            when (errorResponse.responseCode) {
                CODE_TOKEN_EXPIRE -> {
                    response.close()
                    response = handleTokenExpiration(
                        chain = chain,
                        originalRequest = originalRequest,
                        requestAccessToken = localStorage.accessToken
                    )
                }
                // 토큰 만료가 아닌경우 해당 에러코드 그대로 리턴 (각각의 API에서 핸들링해주도록 구현해야 합니다.)
                else -> {
                    response = response.newBuilder().body(newErrorBody).build()
                }
            }
        }
        return response
    }

    private fun Request.newAuthBuilder(): Request {
        val token = localStorage.accessToken

        val formattedToken = if (token.contains(BEARER)) token else "$BEARER$token"
        Timber.d("formattedToken : $formattedToken")

        return this.newBuilder()
            .addHeader(AUTHORIZATION, formattedToken)
            .build()
    }

    private fun handleTokenExpiration(
        chain: Interceptor.Chain,
        originalRequest: Request,
        requestAccessToken: String
    ): Response =
        runBlocking {
            mutex.withLock {
                when (
                    isTokenValid(
                        requestAccessToken = requestAccessToken,
                        currentAccessToken = localStorage.accessToken
                    )
                ) {
                    true -> chain.proceed(originalRequest.newAuthBuilder())
                    false -> handleTokenRefresh(
                        chain = chain,
                        originalRequest = originalRequest,
                        refreshToken = localStorage.refreshToken
                    )
                }
            }
        }

    private fun isTokenValid(requestAccessToken: String, currentAccessToken: String): Boolean =
        requestAccessToken != currentAccessToken && currentAccessToken.isNotBlank()

    private fun handleTokenRefresh(
        chain: Interceptor.Chain,
        originalRequest: Request,
        refreshToken: String
    ): Response =
        getTokenRefresh(
            chain = chain,
            originalRequest = originalRequest,
            refreshToken = refreshToken
        ).let { refreshTokenResponse ->
            when (refreshTokenResponse.isSuccessful) {
                true -> handleTokenRefreshSuccess(
                    chain = chain,
                    originalRequest = originalRequest,
                    refreshTokenResponse = refreshTokenResponse
                )

                false -> handleTokenRefreshFailed(refreshTokenResponse = refreshTokenResponse)
            }
        }

    private fun getTokenRefresh(
        chain: Interceptor.Chain,
        originalRequest: Request,
        refreshToken: String
    ): Response = chain.proceed(
        originalRequest.newBuilder()
            .get()
            .url("${BuildConfig.BASE_URL}$API/$AUTH/$REISSUE")
            .addHeader(AUTHORIZATION, BEARER + refreshToken)
            .build()
    )

    private fun handleTokenRefreshSuccess(
        chain: Interceptor.Chain,
        originalRequest: Request,
        refreshTokenResponse: Response
    ): Response {
        val responseRefreshToken = json.decodeFromString<ApiResponse<ResponseReissueDto>>(
            refreshTokenResponse.body?.string()
                ?: throw IllegalStateException("\"refreshTokenResponse is null $refreshTokenResponse\"")
        )

        with(localStorage) {
            accessToken = responseRefreshToken.result?.accessToken ?: ""
            refreshToken = responseRefreshToken.result?.refreshToken ?: ""
        }

        refreshTokenResponse.close()
        return chain.proceed(originalRequest.newAuthBuilder())
    }

    private fun handleTokenRefreshFailed(refreshTokenResponse: Response): Response {
        refreshTokenResponse.close()

        CoroutineScope(Dispatchers.Main).launch {
            with(context) {
                CoroutineScope(Dispatchers.Main).launch {
                    startActivity(
                        Intent.makeRestartActivityTask(
                            packageManager.getLaunchIntentForPackage(packageName)?.component
                        )
                    )
                }
            }
        }

        localStorage.clear()

        return refreshTokenResponse
    }

    companion object {
        const val HTTP_BAD_REQUEST = 400
        const val CODE_TOKEN_EXPIRE = "TOK_001"
        const val AUTHORIZATION = "Authorization"
        const val BEARER = "Bearer "
    }
}
