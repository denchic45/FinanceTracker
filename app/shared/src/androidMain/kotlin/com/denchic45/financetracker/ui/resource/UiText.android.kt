package com.denchic45.financetracker.ui.resource

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

fun UiText.get(context: Context): String = when (this) {
    is UiText.Raw -> value
    is UiText.ResourceText -> args?.let {
        context.getString(value, *args)
    } ?: context.getString(value)

    is UiText.PluralResourceText -> args?.let {
        context.resources.getString(value, quantity, *args)
    } ?: context.resources.getString(value, quantity)
}

@Composable
@ReadOnlyComposable
actual fun UiText.get(): String {
    LocalConfiguration.current // ensures recomposition on configuration change
    return get(context = LocalContext.current)
}