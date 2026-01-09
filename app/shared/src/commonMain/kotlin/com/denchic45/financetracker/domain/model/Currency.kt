package com.denchic45.financetracker.domain.model

enum class Currency(val symbol: Char) {
    RUB('₽'),
    USD('$'),
    EUR('€'),
    GBP('£');

    companion object {
        fun fromSymbol(symbol: Char): Currency? {
            return entries.find { it.symbol == symbol }
        }
    }
}