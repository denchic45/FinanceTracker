package com.denchic45.financetracker.feature.category

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.left
import arrow.core.right
import arrow.core.some
import com.denchic45.financetracker.category.model.CategoryRequest
import com.denchic45.financetracker.category.model.CategoryResponse
import com.denchic45.financetracker.database.table.CategoryDao
import com.denchic45.financetracker.error.CategoryNotFound
import org.jetbrains.exposed.sql.transactions.transaction

class CategoryRepository() {

    fun add(request: CategoryRequest): CategoryResponse = transaction {
        CategoryDao.new {
            name = request.name
            icon = request.icon
            income = request.income
        }.toCategoryResponse()
    }

    fun findById(categoryId: Long): Either<CategoryNotFound, CategoryResponse> = transaction {
        CategoryDao.findById(categoryId)?.toCategoryResponse()?.right() ?: CategoryNotFound.left()
    }

    fun update(categoryId: Long, request: CategoryRequest): Either<CategoryNotFound, CategoryResponse> = transaction {
        CategoryDao.findById(categoryId)?.apply {
            name = request.name
            icon = request.icon
            income = request.income
        }?.toCategoryResponse()?.right() ?: CategoryNotFound.left()
    }

    fun remove(categoryId: Long): Option<CategoryNotFound> = transaction {
        CategoryDao.findById(categoryId)?.delete() ?: return@transaction CategoryNotFound.some()
        None
    }
}