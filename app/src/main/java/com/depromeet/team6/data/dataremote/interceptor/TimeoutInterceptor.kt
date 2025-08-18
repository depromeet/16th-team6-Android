package com.depromeet.team6.data.dataremote.interceptor

import android.content.Context
import com.depromeet.team6.data.dataremote.model.response.base.ApiException
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException
import timber.log.Timber
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.net.ssl.SSLException

class TimeoutInterceptor @Inject constructor(
    @ApplicationContext context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        var retryCount = 0
        while (retryCount < 3) {
            try {
                return chain.proceed(originalRequest)
            } catch (e: IOException) {
                Timber.e(e)
                // 멱등하지 않은 요청에 대해서는 재시도 하지 않고 throw
                if (!isIdempotentMethod(originalRequest.method)) {
                    throw requestExceptionMapper(e)
                }
                // 재시도가 유효한 특정 예외에 대해서만 재시도 요청
                if (e is SocketTimeoutException || e is ConnectException || e is SocketException) {
                    retryCount++
                    continue
                } else {
                    throw requestExceptionMapper(e)
                }
            }
        }
        try {
            return chain.proceed(originalRequest)
        } catch (e: IOException) {
            Timber.e(e)
            throw requestExceptionMapper(e)
        }
    }

    // 멱등한 Http 메서드에 대해서만 재요청 처리하기 위함
    // 멱등하지 않은 Http 매서드의 경우 여러번 재요청할 시 서버에서 의도된 동작을 하지 않을 수 있습니다.
    private fun isIdempotentMethod(method: String): Boolean {
        return when (method) {
            "POST", "CONNECT", "PATCH" -> false
            else -> true
        }
    }

    private fun requestExceptionMapper(e: IOException): ApiException.NetworkFailureException {
        return when (e) {
            is SocketTimeoutException, is InterruptedIOException -> ApiException.NetworkFailureException.Timeout
            is ConnectException, is SocketException, is SSLException -> ApiException.NetworkFailureException.CannotFindHost
            is UnknownHostException -> ApiException.NetworkFailureException.NoConnection
            else -> ApiException.NetworkFailureException.CannotFindHost
        }
    }
}
