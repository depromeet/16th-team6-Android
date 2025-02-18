package com.depromeet.team6.data.dataremote.interceptor

import android.app.Application
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class DummyInterceptor @Inject constructor(
    private val json: Json,
    private val context: Application
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        TODO("Not yet implemented")
    }
}
