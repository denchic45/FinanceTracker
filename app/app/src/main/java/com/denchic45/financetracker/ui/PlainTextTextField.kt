package com.denchic45.financetracker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun PlainTextTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    placeholderText: String? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val textColor = if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        textStyle.color
    }

    Box(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            textStyle = textStyle.copy(color = textColor),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions
        )

        // Show the placeholder if the value is empty and placeholderText is provided
        if (value.isEmpty() && !placeholderText.isNullOrEmpty()) {
            Text(
                text = placeholderText,
                modifier = Modifier.fillMaxWidth(),
                style = textStyle.copy(
                    // Use a slightly faded color for the placeholder
                    color = textStyle.color.copy(alpha = 0.4f)
                )
            )
        }
    }
}