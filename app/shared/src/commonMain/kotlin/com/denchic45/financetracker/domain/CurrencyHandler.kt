package com.denchic45.financetracker.domain

import com.denchic45.financetracker.domain.model.Currency
import java.text.DecimalFormat
import java.text.NumberFormat

interface CurrencyHandler {
    val decimalSeparator: Char
    fun parse(value: String): Double?
    fun formatForEditing(amount: Long): String
    fun formatForDisplay(amount: Long, currency: Currency): String
    fun filterInput(input: String): String?
    fun toLong(value: String): Long

}

class JVMCurrencyHandler(
    locale: java.util.Locale = java.util.Locale.getDefault()
) : CurrencyHandler {
    private val symbols = java.text.DecimalFormatSymbols.getInstance(locale).apply {
        groupingSeparator = ' '
    }

    private val integerFormat = DecimalFormat("#,##0", symbols)
    private val fractionalFormat = DecimalFormat("#,##0.00", symbols)
    private val localeFormat = NumberFormat.getCurrencyInstance(locale) as DecimalFormat

    override val decimalSeparator: Char = symbols.decimalSeparator

    override fun parse(value: String): Double? {
        val sanitized = value.replace(decimalSeparator, '.')
        return sanitized.toDoubleOrNull()
    }

    override fun formatForEditing(amount: Long): String {
        if (amount == 0L) return ""
        val amount = amount / 100.0
        val editingFormat = (getFormatter(amount).clone() as DecimalFormat).apply {
            isGroupingUsed = false
            minimumFractionDigits = 0
        }
        return editingFormat.format(amount)
    }

    override fun formatForDisplay(amount: Long, currency: Currency): String {
        val amount = amount / 100.0
        val currentSymbol = currency.symbol.toString()
        val formatter = getFormatter(amount).clone() as DecimalFormat

        formatter.apply {
            positivePrefix = localeFormat.positivePrefix.replace(
                localeFormat.decimalFormatSymbols.currencySymbol,
                currentSymbol
            )
            negativePrefix = localeFormat.negativePrefix.replace(
                localeFormat.decimalFormatSymbols.currencySymbol,
                currentSymbol
            )
            positiveSuffix = localeFormat.positiveSuffix.replace(
                localeFormat.decimalFormatSymbols.currencySymbol,
                currentSymbol
            )
            negativeSuffix = localeFormat.negativeSuffix.replace(
                localeFormat.decimalFormatSymbols.currencySymbol,
                currentSymbol
            )
        }

        return formatter.format(amount).replace("\u00A0", " ")
    }

    private fun getFormatter(amount: Double): DecimalFormat {
        return if (amount % 1.0 == 0.0) integerFormat else fractionalFormat
    }

    override fun filterInput(input: String): String? {
        val cleanInput = input.replace(" ", "")
        val wrongSeparator = if (decimalSeparator == ',') '.' else ','
        val sanitized = cleanInput.replace(wrongSeparator, decimalSeparator)

        val regex = Regex("""^\d*(\Q$decimalSeparator\E\d{0,2})?$""")

        return sanitized.takeIf { sanitized.matches(regex) || sanitized.isEmpty() }
    }

    override fun toLong(value: String): Long {
        if (value.isEmpty()) return 0L
        val standardized = value.replace(decimalSeparator, '.')
        val doubleValue = standardized.toDoubleOrNull() ?: 0.0
        return kotlin.math.round(doubleValue * 100).toLong()
    }
}