package com.depromeet.team6.presentation.util.toast

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.depromeet.team6.R

fun atChaTextButtonToastMessage(
    context: Context,
    @StringRes messageResId: Int,
    @StringRes buttonTextResId: Int,
    length: Int = Toast.LENGTH_SHORT,
    onClick:()->Unit
) {
    val layoutInflater = LayoutInflater.from(context)
    val layout = layoutInflater.inflate(R.layout.atcha_text_button_toast, null)

    val textView = layout.findViewById<TextView>(R.id.atcha_toast_Message)
    textView.text = context.getString(messageResId)

    val textButton = layout.findViewById<TextView>(R.id.atcha_toast_text_button)
    textButton.text = context.getString(buttonTextResId)

    val toastContainer = FrameLayout(context).apply {
        setPadding(16, 0, 16, 0)
        addView(layout)
    }

    val toast = Toast(context).apply {
        duration = length
        view = toastContainer
    }

    layout.setOnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            onClick()
            toast.cancel()
            v.performClick()
            true
        } else {
            false
        }
    }


    val yOffsetDp = 12
    val yOffsetPx = (yOffsetDp * context.resources.displayMetrics.density).toInt()
    toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, yOffsetPx)

    toast.show()

    val toastDurationMs = when (length) {
        Toast.LENGTH_SHORT -> 2000L
        Toast.LENGTH_LONG -> 3500L
        else -> 2000L
    }

    layout.postDelayed({
        val slideUp = TranslateAnimation(0f, 0f, 0f, -(layout.height + yOffsetPx).toFloat()).apply {
            duration = 500L
            fillAfter = true
        }
        layout.startAnimation(slideUp)
    }, toastDurationMs - 500L)
}
