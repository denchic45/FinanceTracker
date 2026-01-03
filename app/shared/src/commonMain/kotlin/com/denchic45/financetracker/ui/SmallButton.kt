package com.denchic45.financetracker.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SmallButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.widthIn(min=40.dp).height(32.dp),
        enabled = enabled,
        colors = colors,
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(4.dp)
    ) {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.labelLarge) {
            content()
        }
    }
}

@Composable
fun SmallPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit
) {
    SmallButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(),
        content = text
    )
}

@Composable
fun SmallSecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit
) {
    SmallButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.elevatedButtonColors(),
        content = text
    )
}

@Composable
fun SmallTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit
) {
    SmallButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(),
        content = text
    )
}

@Preview
@Composable
fun SmallPrimaryButtonPreview() {
    SmallPrimaryButton(onClick = {}) {
        Text("Primary")
    }
}

@Preview
@Composable
fun SmallSecondaryButtonPreview() {
    SmallSecondaryButton(onClick = {}) {
        Text("Secondary")
    }
}

@Preview
@Composable
fun SmallTextButtonPreview() {
    SmallTextButton(onClick = {}) {
        Text("Text")
    }
}
