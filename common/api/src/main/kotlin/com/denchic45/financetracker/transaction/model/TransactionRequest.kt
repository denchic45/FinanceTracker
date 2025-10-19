package com.denchic45.financetracker.transaction.model

import com.denchic45.financetracker.util.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class TransactionRequest(
    @Serializable(LocalDateTimeSerializer::class)
    val datetime: LocalDateTime,
    val amount: Long,
    val type: TransactionType,
    val description: String,
    val accountId: Long,
    val categoryId: Long,
    val incomeSourceId: Long? = null
)
