package com.denchic45.financetracker.api.transaction.model

import com.denchic45.financetracker.api.account.model.AccountType
import com.denchic45.financetracker.api.category.model.CategoryResponse
import com.denchic45.financetracker.api.tag.model.TagResponse
import com.denchic45.financetracker.util.UUIDSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
sealed class AbstractTransactionResponse() {
    abstract val id: Long
    abstract val datetime: LocalDateTime
    abstract val amount: Long
    abstract val note: String
    abstract val account: TransactionAccount
}

@Serializable
data class TransactionResponse(
    override val id: Long,
    override val datetime: LocalDateTime,
    override val amount: Long,
    override val note: String,
    override val account: TransactionAccount,
    val category: CategoryResponse,
    val tags: List<TagResponse>,
    val income: Boolean
) : AbstractTransactionResponse()

@Serializable
data class TransferTransactionResponse(
    override val id: Long,
    override val datetime: LocalDateTime,
    override val amount: Long,
    override val note: String,
    override val account: TransactionAccount,
    val incomeAccount: TransactionAccount,
) : AbstractTransactionResponse()

@Serializable
data class TransactionAccount(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val accountType: AccountType,
    val initialBalance: Long,
    val balance: Long
)