package com.denchic45.financetracker.api.category.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    val id: Long,
    val name: String,
    val iconName: String,
    val income: Boolean
)