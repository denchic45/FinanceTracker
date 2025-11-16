package com.denchic45.financetracker.ui.resource

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import com.denchic45.financetracker.ui.icon.AllIconsNamed
import com.denchic45.financetracker.ui.icon.AppIcons


sealed class UiImage {

    data class Resource(val value: Int) : UiImage()

    data class ImageName(val name: String) : UiImage()
}

inline fun UiImage.onResource(fn: (value: Int) -> Unit): UiImage = apply {
    if (this is UiImage.Resource) fn(value)
}

inline fun UiImage.onIconName(fn: (value: String) -> Unit): UiImage = apply {
    if (this is UiImage.ImageName) fn(name)
}


fun uiIconOf(value: Int) = UiImage.Resource(value)

fun uiIconOf(iconName: String) = UiImage.ImageName(iconName)

@Composable
fun UiImage.getPainter(): Painter = when (this) {
    is UiImage.Resource -> painterResource(id = value)
    is UiImage.ImageName -> rememberVectorPainter(AppIcons.AllIconsNamed.getValue(name))
}