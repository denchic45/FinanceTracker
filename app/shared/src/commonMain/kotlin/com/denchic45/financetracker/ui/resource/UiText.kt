package com.denchic45.financetracker.ui.resource

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

sealed class UiText {
    class Raw(val value: String) : UiText()

    class ResourceText(val value: StringResource, val args: Array<Any>?) : UiText()

    class PluralResourceText(
        val value: StringResource,
        val quantity: Int,
        val args: Array<Any>?
    ) : UiText() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as PluralResourceText

            if (quantity != other.quantity) return false
            if (value != other.value) return false
            if (!args.contentEquals(other.args)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = quantity
            result = 31 * result + value.hashCode()
            result = 31 * result + (args?.contentHashCode() ?: 0)
            return result
        }
    }
}

inline fun UiText.onString(block: (String) -> Unit): UiText = apply {
    if (this is UiText.Raw) block(value)
}

inline fun UiText.onResource(block: (StringResource) -> Unit): UiText = apply {
    if (this is UiText.ResourceText) block(value)
}

fun uiTextOf(value: String) = UiText.Raw(value)

fun uiTextOf(value: StringResource) = UiText.ResourceText(value, null)

fun uiTextOf(value: StringResource, args: Array<Any>) = UiText.ResourceText(value, args)

fun uiTextOf(value: StringResource, quantity: Int) =
    UiText.PluralResourceText(value, quantity, null)

fun uiTextOf(value: StringResource, quantity: Int, args: Array<Any>) =
    UiText.PluralResourceText(value, quantity, args)

@Composable
fun UiText.get(): String = when (this) {
    is UiText.Raw -> value
    is UiText.ResourceText -> args?.let {
        stringResource(value, *args)
    } ?: stringResource(value)

    is UiText.PluralResourceText -> args?.let {
        stringResource(value, quantity, *args)
    } ?: stringResource(value, quantity)
}

suspend fun UiText.getSuspended(): String = when (this) {
    is UiText.Raw -> value
    is UiText.ResourceText -> args?.let {
        getString(value, *args)
    } ?: getString(value)

    is UiText.PluralResourceText -> args?.let {
        getString(value, quantity, *args)
    } ?: getString(value, quantity)
}