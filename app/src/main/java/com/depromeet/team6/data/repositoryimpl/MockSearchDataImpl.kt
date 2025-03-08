package com.depromeet.team6.data.repositoryimpl

import android.content.Context
import com.depromeet.team6.data.dataremote.model.response.itinerary.RouteResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MockSearchDataImpl @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun loadMockData(resId: Int): RouteResponse? {
        val inputStream = context.resources.openRawResource(resId)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return Gson().fromJson(jsonString, object : TypeToken<RouteResponse>() {}.type)
    }
}
