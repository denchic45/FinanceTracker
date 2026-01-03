package com.denchic45.financetracker.ui.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val fractionalFormat = DecimalFormat("#,##0.00")
private val integerFormat = DecimalFormat("#,##0")

private val simpleFormat = DecimalFormat("#.##")
fun Long.convertToCurrency(): String = convertToMonetaryFormat('₽')

fun Long.convertToAmount(): String {
    return simpleFormat.format(this / 100.0)
}

fun Long.convertToMonetaryFormat(currency: Char? = null): String {
    val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
        groupingSeparator = ' '
        decimalSeparator = ','
    }
    val amount = this / 100.0
    val isInteger = amount % 1.0 == 0.0
    val format = if (isInteger) integerFormat else fractionalFormat
    format.apply {
        positiveSuffix = currency?.let { " $it" }.orEmpty()
        negativeSuffix = currency?.let { " $it" }.orEmpty()
        format.decimalFormatSymbols = symbols
    }
    return format.format(amount)
}

val currencyRegex = Regex("^\\d*(\\.\\d{0,2})?$")