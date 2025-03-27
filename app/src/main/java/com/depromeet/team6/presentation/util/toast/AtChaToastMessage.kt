package com.depromeet.team6.presentation.util.toast

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.depromeet.team6.R

fun atChaToastMessage(
    context: Context,
    @StringRes messageResId: Int,
    length: Int = Toast.LENGTH_SHORT
) {
    val layoutInflater = LayoutInflater.from(context)
    val layout = layoutInflater.inflate(R.layout.atcha_toast, null)

    val textView = layout.findViewById<TextView>(R.id.atcha_toast_Message)
    textView.text = context.getString(messageResId)

    val toastContainer = FrameLayout(context).apply {
        setPadding(16, 0, 16, 0)
        addView(layout)
    }

    val toast = Toast(context).apply {
        duration = length
        view = toastContainer
    }

    val yOffsetDp = 12
    val yOffsetPx = (yOffsetDp * context.resources.displayMetrics.density).toInt()
    toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, yOffsetPx)

//    val slideDown = TranslateAnimation(0f, 0f, -100f, 0f).apply {
//        duration = 300L
//        fillAfter = true
//    }
//    layout.startAnimation(slideDown)

    toast.show()

    val toastDurationMs = when (length) {
        Toast.LENGTH_SHORT -> 2000L
        Toast.LENGTH_LONG -> 3500L
        else -> 2000L
    }

    layout.postDelayed({
        val slideUp = TranslateAnimation(0f, 0f, 0f, -layout.height.toFloat()).apply {
            duration = 500L
            fillAfter = true
        }
        layout.startAnimation(slideUp)
    }, toastDurationMs - 500L)
}
