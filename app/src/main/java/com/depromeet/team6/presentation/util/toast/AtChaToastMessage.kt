package com.depromeet.team6.presentation.util.toast

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.depromeet.team6.R

fun atChaToastMessage(context: Context, @StringRes messageResId: Int, length: Int = Toast.LENGTH_SHORT) {
    val layoutInflater = LayoutInflater.from(context)
    val layout = layoutInflater.inflate(R.layout.atcha_toast, null)

    val animationStartTime = length - 500L
    val animationDuration = 500L

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

    val yOffsetDp = 12 // 원하는 dp 값
    val yOffsetPx = (yOffsetDp * context.resources.displayMetrics.density).toInt()
    toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, yOffsetPx)


    // Fade out 애니메이션
    val fadeOut = AlphaAnimation(1f, 0f).apply {
        startOffset = animationStartTime
        duration = animationDuration
        fillAfter = true
    }

    toast.view?.startAnimation(fadeOut)

    toast.show()
}
