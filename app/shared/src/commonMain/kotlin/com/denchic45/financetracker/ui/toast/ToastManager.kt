package com.denchic45.financetracker.ui.toast

import com.denchic45.financetracker.ui.resource.UiText

expect class ToastManager {
    fun showToast(message: UiText, toastDurationType: ToastDurationType)
}