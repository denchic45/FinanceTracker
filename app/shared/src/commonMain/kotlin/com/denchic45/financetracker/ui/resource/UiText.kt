package com.denchic45.financetracker.ui.resource

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

sealed class UiText {
    class Raw(val value: String) : UiText()

    class ResourceText(val value: Int, val args: Array<Any>?) : UiText()

    class PluralResourceText(
        val value: Int,
        val quantity: Int,
        val args: Array<Any>?
    ) : UiText() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as PluralResourceText

            if (value != other.value) return false
            if (quantity != other.quantity) return false
            if (!args.contentEquals(other.args)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = value
            result = 31 * result + quantity
            result = 31 * result + (args?.contentHashCode() ?: 0)
            return result
        }
    }
}

inline fun UiText.onString(fn: (success: String) -> Unit): UiText = apply {
    if (this is UiText.Raw) fn(value)
}

inline fun UiText.onResource(fn: (failure: Int) -> Unit): UiText = apply {
    if (this is UiText.ResourceText) fn(value)
}

fun uiTextOf(value: String) = UiText.Raw(value)

fun uiTextOf(value: Int) = UiText.ResourceText(value, null)

fun uiTextOf(value: Int, args: Array<Any>) = UiText.ResourceText(value, args)

fun uiTextOf(value: Int, quantity: Int) = UiText.PluralResourceText(value, quantity, null)

fun uiTextOf(value: Int, quantity: Int, args: Array<Any>) =
    UiText.PluralResourceText(value, quantity, args)

@Composable
@ReadOnlyComposable
expect fun UiText.get(): String