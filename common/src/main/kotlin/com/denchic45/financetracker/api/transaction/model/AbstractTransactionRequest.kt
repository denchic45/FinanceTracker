package com.denchic45.financetracker.api.transaction.model

import com.denchic45.financetracker.util.UUIDSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
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
    override val datetime: LocalDateTime,
    override val amount: Long,
    override val note: String,
    @Serializable(UUIDSerializer::class)
    override val accountId: UUID,
    val categoryId: Long,
    val tagIds: List<Long> = emptyList()
) : AbstractTransactionRequest()

@Serializable
data class TransferTransactionRequest(
    override val datetime: LocalDateTime,
    override val amount: Long,
    override val note: String,
    @Serializable(UUIDSerializer::class)
    override val accountId: UUID,
    @Serializable(UUIDSerializer::class)
    val incomeSourceId: UUID
) : AbstractTransactionRequest()