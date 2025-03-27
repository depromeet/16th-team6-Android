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

        when (response.code) {
            CODE_TOKEN_EXPIRE -> {
                response.close()
                response = handleTokenExpiration(
                    chain = chain,
                    originalRequest = originalRequest,
                    requestAccessToken = localStorage.accessToken
                )
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
        const val CODE_TOKEN_EXPIRE = 400
        const val AUTHORIZATION = "Authorization"
        const val BEARER = "Bearer "
    }
}
