package com.depromeet.team6.presentation.util.toast

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.depromeet.team6.R

fun atChaToastMessage(context: Context, @StringRes messageResId: Int, length: Int = Toast.LENGTH_SHORT) {
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

    toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 12)
    toast.show()
}
