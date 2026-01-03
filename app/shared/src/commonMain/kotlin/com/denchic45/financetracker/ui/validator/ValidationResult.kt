package com.denchic45.financetracker.ui.validator

fun interface ValidationResult {
    operator fun invoke(isValid: Boolean)
}