package com.denchic45.financetracker.data.mapper

import com.denchic45.financetracker.api.category.model.CategoryResponse
import com.denchic45.financetracker.data.database.entity.CategoryEntity
import com.denchic45.financetracker.domain.model.CategoryItem

fun List<CategoryResponse>.toCategoryEntities() = map { response ->
    response.toCategoryEntity()
}

fun CategoryResponse.toCategoryEntity() = CategoryEntity(
    id = id,
    name = name,
    icon = icon,
    income = income
)

fun CategoryEntity.toCategoryResponse() = CategoryResponse(
    id = id,
    name = name,
    icon = icon,
    income = income
)

fun List<CategoryEntity>.toCategoryResponses() = map(CategoryEntity::toCategoryResponse)

fun CategoryEntity.toCategoryItem() = CategoryItem(
    id = id,
    name = name,
    iconName = icon,
    income = income
)

fun List<CategoryEntity>.toCategoryItems() = map(CategoryEntity::toCategoryItem)