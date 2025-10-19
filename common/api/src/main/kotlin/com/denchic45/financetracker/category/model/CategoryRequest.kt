package com.denchic45.financetracker.category.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryRequest(
    val name: String,
    val icon: String,
    val income: Boolean
)