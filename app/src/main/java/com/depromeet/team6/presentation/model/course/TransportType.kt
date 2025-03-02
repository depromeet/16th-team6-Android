package com.depromeet.team6.presentation.model.course

import android.annotation.SuppressLint
import android.content.Context
import com.depromeet.team6.R

enum class TransportType(val arrayId: Int) {
    WALK(R.array.transport_type_walk),
    BUS(R.array.transport_type_bus),
    SUBWAY(R.array.transport_type_subway);

    fun getTransportSubtypeResourceId(context: Context, idx: Int): Int {
        return context.resources.obtainTypedArray(arrayId).use { typedArray ->
            typedArray.getResourceId(idx, -1)
        }
    }
}