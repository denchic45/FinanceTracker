package com.denchic45.financetracker.feature.category

import com.denchic45.financetracker.database.table.CategoryDao
import com.denchic45.financetracker.category.model.CategoryResponse

fun CategoryDao.toCategoryResponse() = CategoryResponse(
    id=id.value,
    name = name,
    icon = icon,
    income = income
)

fun Iterable<CategoryDao>.toCategoryResponses() = map(CategoryDao::toCategoryResponse)