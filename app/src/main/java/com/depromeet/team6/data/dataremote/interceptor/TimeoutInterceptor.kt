package com.depromeet.team6.data.dataremote.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class TimeoutInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)
    }
}