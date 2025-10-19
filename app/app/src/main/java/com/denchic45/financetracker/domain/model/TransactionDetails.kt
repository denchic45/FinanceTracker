package com.denchic45.financetracker.domain.model

import java.time.LocalDateTime

data class TransactionDetails(
    val item: TransactionItem,
    val datetime: LocalDateTime,
    val description: String
)