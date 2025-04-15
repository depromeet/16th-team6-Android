package com.depromeet.team6.presentation.util.amplitude

import android.content.Context
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.depromeet.team6.BuildConfig

object AmplitudeUtils {
    private lateinit var amplitude: Amplitude

    fun initAmplitude(context: Context) {
        amplitude = Amplitude(
            Configuration(
                apiKey = BuildConfig.AMPLITUDE_API_KEY,
                context = context
            )
        )
    }

    fun trackEvent(eventName: String) {
        amplitude.track(eventType = eventName).flush()
    }

    fun <T> trackEventWithProperty(eventName: String, propertyName: String, propertyValue: T) {
        amplitude.track(
            eventType = eventName,
            eventProperties = mapOf(propertyName to propertyValue)
        ).flush()
    }

    fun trackEventWithProperties(eventName: String, properties: Map<String, Any>) {
        amplitude.track(eventType = eventName, eventProperties = properties).flush()
    }

    fun setUserId(userId: String) {
        if (::amplitude.isInitialized) {
            amplitude.setUserId(userId)
        }
    }
}
