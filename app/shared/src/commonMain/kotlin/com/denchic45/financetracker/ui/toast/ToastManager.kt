package com.denchic45.financetracker.ui.toast

expect class ToastManager {
    suspend fun showToast(message: String, toastDurationType: ToastDurationType)
}