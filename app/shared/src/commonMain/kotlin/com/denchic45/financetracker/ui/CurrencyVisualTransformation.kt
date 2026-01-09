package com.denchic45.financetracker.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.denchic45.financetracker.domain.model.Currency

class CurrencyVisualTransformation(
    private val decimalSeparator: Char,
    private val currency: Currency
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val parts = originalText.split(decimalSeparator)
        val integerPart = parts[0]

        val formattedInteger = integerPart.reversed()
            .chunked(3)
            .joinToString(" ")
            .reversed()

        val formattedText = if (parts.size > 1) {
            "$formattedInteger$decimalSeparator${parts[1]}"
        } else {
            formattedInteger
        }

        val out = "$formattedText ${currency.symbol}"

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset == 0) return 0
                val spacesBefore = formattedText.take(
                    findTransformedIndex(originalText, formattedText, offset)
                ).count { it == ' ' }

                return offset + spacesBefore
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset >= formattedText.length) return originalText.length

                val textBefore = out.take(offset)
                val spacesBefore = textBefore.count { it == ' ' }

                return (offset - spacesBefore).coerceIn(0, originalText.length)
            }

            private fun findTransformedIndex(orig: String, trans: String, offset: Int): Int {
                var origCount = 0
                trans.forEachIndexed { index, char ->
                    if (char != ' ') origCount++
                    if (origCount == offset) return index + 1
                }
                return trans.length
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}