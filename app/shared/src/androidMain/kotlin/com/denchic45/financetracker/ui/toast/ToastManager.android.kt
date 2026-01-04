package com.denchic45.financetracker.ui.toast

import android.content.Context
import android.widget.Toast
import com.denchic45.financetracker.ui.resource.UiText
import com.denchic45.financetracker.ui.resource.get

actual open class ToastManager(private val context: Context) {
    actual fun showToast(message: UiText, toastDurationType: ToastDurationType) {
        val duration = when (toastDurationType) {
            ToastDurationType.SHORT -> Toast.LENGTH_SHORT
            ToastDurationType.LONG -> Toast.LENGTH_LONG
        }
        Toast.makeText(context, message.get(context), duration).show()
    }
}