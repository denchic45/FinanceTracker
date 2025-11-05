package com.denchic45.financetracker.api.statistic.model

import kotlinx.serialization.Serializable

@Serializable
data class TotalsAmount(
    val expenses: Long,
    val incomes: Long,
    val profit: Long
)