package com.denchic45.financetracker.transaction.model

import com.denchic45.financetracker.util.LocalDateTimeSerializer
import com.denchic45.financetracker.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class TransactionRequest(
    @Serializable(LocalDateTimeSerializer::class)
    val datetime: LocalDateTime,
    val amount: Long,
    val type: TransactionType,
    val description: String,
    @Serializable(UUIDSerializer::class)
    val accountId: UUID,
    val categoryId: Long,
    @Serializable(UUIDSerializer::class)
    val incomeSourceId: UUID? = null
)
