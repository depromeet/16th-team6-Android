package com.depromeet.team6.presentation.util.amplitude

import android.content.Context
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.depromeet.team6.BuildConfig
import timber.log.Timber

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
        if (::amplitude.isInitialized) {
            Timber.d("Amplitude EventName: $eventName, Properties: $properties")
            amplitude.track(eventType = eventName, eventProperties = properties).flush()
        } else {
            Timber.w("TrackEventWithProperties Failed Amplitude not initialized")
        }
    }

    fun setUserId(userId: String) {
        if (::amplitude.isInitialized) {
            Timber.d("Amplitude setUserId: $userId")
            amplitude.setUserId(userId)
        } else {
            Timber.w("SetUserId Failed Amplitude not initialized")

        }
    }
}
