package com.denchic45.financetracker.api.transaction.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
sealed class AbstractTransactionRequest {
    abstract val datetime: LocalDateTime
    abstract val amount: Long
    abstract val note: String
    abstract val accountId: Uuid
}

@Serializable
data class TransactionRequest(
    val income: Boolean,
    override val datetime: LocalDateTime,
    override val amount: Long,
    override val note: String,
    override val accountId: Uuid,
    val categoryId: Long,
    val tagIds: List<Long> = emptyList()
) : AbstractTransactionRequest()

@Serializable
data class TransferTransactionRequest(
    override val datetime: LocalDateTime,
    override val amount: Long,
    override val note: String,
    override val accountId: Uuid,
    val incomeSourceId: Uuid
) : AbstractTransactionRequest()