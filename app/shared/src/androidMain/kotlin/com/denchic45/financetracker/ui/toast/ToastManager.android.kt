package com.denchic45.financetracker.ui.toast

import android.content.Context
import android.widget.Toast

actual open class ToastManager(private val context: Context) {
    actual suspend fun showToast(message: String, toastDurationType: ToastDurationType) {
        val duration = when (toastDurationType) {
            ToastDurationType.SHORT -> Toast.LENGTH_SHORT
            ToastDurationType.LONG -> Toast.LENGTH_LONG
        }
        Toast.makeText(context, message, duration).show()
    }
}