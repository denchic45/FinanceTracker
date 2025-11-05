package com.denchic45.financetracker.api.statistic.model

import com.denchic45.financetracker.api.account.model.AccountResponse
import kotlinx.serialization.Serializable

@Serializable
data class AccountStatisticsResponse(
    val account: AccountResponse,
    val expenses: Long,
    val incomes: Long,
    val profit: Long
)