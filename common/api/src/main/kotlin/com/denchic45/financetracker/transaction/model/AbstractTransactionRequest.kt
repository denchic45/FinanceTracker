package com.denchic45.financetracker.transaction.model

import com.denchic45.financetracker.util.LocalDateTimeSerializer
import com.denchic45.financetracker.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
sealed class AbstractTransactionRequest {
    abstract val datetime: LocalDateTime
    abstract val amount: Long
    abstract val note: String
    abstract val accountId: UUID
}

@Serializable
data class TransactionRequest(
    val income: Boolean,
    @Serializable(LocalDateTimeSerializer::class)
    override val datetime: LocalDateTime,
    override val amount: Long,
    override val note: String,
    @Serializable(UUIDSerializer::class)
    override val accountId: UUID,
    val categoryId: Long,
) : AbstractTransactionRequest()

@Serializable
data class TransferTransactionRequest(
    @Serializable(LocalDateTimeSerializer::class)
    override val datetime: LocalDateTime,
    override val amount: Long,
    override val note: String,
    @Serializable(UUIDSerializer::class)
    override val accountId: UUID,
    @Serializable(UUIDSerializer::class)
    val incomeSourceId: UUID
) : AbstractTransactionRequest()