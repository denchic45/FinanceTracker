package com.denchic45.financetracker.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Don't show the symbol if the text is empty
        if (text.text.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        // The text to display, e.g., "123.45 ₽"
        val transformed = text + AnnotatedString(" ₽")

        // The OffsetMapping is crucial. It tells Compose how to map cursor
        // positions from the transformed text back to the original text.
        // Since we are only adding characters at the end, the mapping is
        // one-to-one (identity) for the original text length.
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // The cursor position in the transformed text is the same as the original.
                return offset
            }

            override fun transformedToOriginal(offset: Int): Int {
                // If the user tries to place the cursor within or after " ₽",
                // move it back to the end of the actual number.
                if (offset >= text.length) {
                    return text.length
                }
                return offset
            }
        }

        return TransformedText(transformed, offsetMapping)
    }
}