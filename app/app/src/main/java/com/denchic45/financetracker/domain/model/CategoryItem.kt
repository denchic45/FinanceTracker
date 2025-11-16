package com.denchic45.financetracker.domain.model


data class CategoryItem(
    val id: Long,
    val name: String,
    val iconName: String,
    val income: Boolean
)