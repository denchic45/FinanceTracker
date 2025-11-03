package com.denchic45.financetracker.statistic.model

import com.denchic45.financetracker.tag.model.TagResponse
import kotlinx.serialization.Serializable


@Serializable
data class TagAmount(
    val item: TagResponse,
    val expenses: Long,
    val incomes: Long,
    val count: Int
)