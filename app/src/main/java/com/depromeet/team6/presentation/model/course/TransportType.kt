package com.depromeet.team6.presentation.model.course

import android.content.Context
import com.depromeet.team6.R

enum class TransportType(val arrayId: Int) {
    WALK(R.array.transport_type_walk),
    BUS(R.array.transport_type_bus),
    SUBWAY(R.array.transport_type_subway);

    fun getTransportSubtypeResourceId(context: Context, idx: Int): Int {
        val typedArray = context.resources.obtainTypedArray(arrayId)
        try {
            return typedArray.getResourceId(idx, -1)
        } finally {
            typedArray.recycle()
        }
    }

    fun getTransportSubtypeColor(context: Context, idx : Int): Int{
        val typedArray = context.resources.obtainTypedArray(arrayId)
        try {
            return typedArray.getColor(idx, -1)
        } finally {
            typedArray.recycle()
        }
    }
}
